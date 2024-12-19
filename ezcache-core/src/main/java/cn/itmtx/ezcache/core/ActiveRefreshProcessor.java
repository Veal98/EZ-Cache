package cn.itmtx.ezcache.core;

import cn.itmtx.ezcache.common.annotation.EzCache;
import cn.itmtx.ezcache.common.bo.CacheKeyBo;
import cn.itmtx.ezcache.common.bo.CacheWrapper;
import cn.itmtx.ezcache.core.proxy.ICacheProxy;
import cn.itmtx.ezcache.common.bo.EzCacheConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 主动刷新缓存处理器
 */
public class ActiveRefreshProcessor {

    private static final Logger log = LoggerFactory.getLogger(ActiveRefreshProcessor.class);

    public static final long ALARM_REFRESH_TIME_MILLIS_THRESHOLD = 10 * 60 * 1000;

    private static final int MIN_REFRESH_TIME_MILLIS = 2 * 60 * 1000;

    private final CacheProcessor cacheProcessor;

    /**
     * 用于主动刷新缓存的线程池
     */
    private final ThreadPoolExecutor activeRefreshThreadPoolExecutor;

    /**
     * 正在进行主动刷新缓存的队列
     * key: cache key, value: 随便。目的是为了使用 map.putIfAbsent 方法，所以才用了 Map, value 只要不为空就可以
     */
    private final ConcurrentHashMap<CacheKeyBo, Byte> activeRefreshingMap;

    public ActiveRefreshProcessor(CacheProcessor cacheProcessor, EzCacheConfig ezCacheConfig) {
        this.cacheProcessor = cacheProcessor;

        int queueCapacity = ezCacheConfig.getAsyncRefreshQueueCapacity();
        activeRefreshingMap = new ConcurrentHashMap<CacheKeyBo, Byte>(queueCapacity);

        // 阻塞队列
        LinkedBlockingQueue<Runnable> activeRefreshingQueue = new LinkedBlockingQueue<Runnable>(queueCapacity);
        // 拒绝策略
        RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
        int corePoolSize = ezCacheConfig.getAsyncRefreshThreadPoolSize();
        int maximumPoolSize = ezCacheConfig.getAsyncRefreshThreadPoolMaxSize();
        int keepAliveTimeMillis = ezCacheConfig.getAsyncRefreshThreadPoolKeepAliveTimeMillis();
        activeRefreshThreadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTimeMillis, TimeUnit.MILLISECONDS,
                activeRefreshingQueue,
                new ThreadFactory() {
                    private final AtomicInteger threadNumber = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        String namePrefix = "EzCache-ActiveRefreshHandler(Async)-";
                        Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
                        t.setDaemon(true);
                        return t;
                    }
                },
                rejectedExecutionHandler);
    }

    /**
     * 异步刷新
     * 判断缓存是否快要过期，若快过期则刷新缓存
     * @param proxy
     * @param ezCache
     * @param cacheKeyBo
     * @param cacheWrapper
     */
    public void asyncRefresh(ICacheProxy proxy, EzCache ezCache, CacheKeyBo cacheKeyBo, CacheWrapper<Object> cacheWrapper) {
        // 判断是否可以执行刷新
        if (!this.isExecuteRefresh(ezCache, cacheKeyBo, cacheWrapper)) {
            return ;
        }

        // 往 activeRefreshingMap 中添加主动刷新任务
        if (null == activeRefreshingMap.putIfAbsent(cacheKeyBo, (byte) 1)) {
            try {
                activeRefreshThreadPoolExecutor.execute(new AsyncRefreshTask(proxy, ezCache, cacheKeyBo, cacheWrapper));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 同步刷新
     * 判断缓存是否快要过期，若快过期则刷新缓存
     * @param proxy
     * @param ezCache
     * @param cacheKeyBo
     * @param cacheWrapper
     */
    public void refresh(ICacheProxy proxy, EzCache ezCache, CacheKeyBo cacheKeyBo, CacheWrapper<Object> cacheWrapper) {
        // 判断是否可以执行刷新
        if (!this.isExecuteRefresh(ezCache, cacheKeyBo, cacheWrapper)) {
            return ;
        }

        // 往 activeRefreshingMap 中添加主动刷新任务
        if (null == activeRefreshingMap.putIfAbsent(cacheKeyBo, (byte) 1)) {
            reallyRefresh(proxy, ezCache, cacheKeyBo, cacheWrapper);
        }
    }

    /**
     * 判断是否可以执行刷新
     * @return
     */
    private boolean isExecuteRefresh(EzCache ezCache, CacheKeyBo cacheKeyBo, CacheWrapper<Object> cacheWrapper) {
        long expireMillis = cacheWrapper.getExpireMillis();

        // 如果过期时间太小了，则不进行刷新，避免刷新过于频繁，影响系统稳定性
        if (expireMillis < MIN_REFRESH_TIME_MILLIS) {
            return false;
        }

        // 计算超时时间
        long alarmTimeMillis = ezCache.refreshAlarmTimeMillis();
        long timeout;
        if (alarmTimeMillis > 0 && alarmTimeMillis < expireMillis) {
            timeout = expireMillis - alarmTimeMillis;
        } else {
            // 如果没有设置预警主动刷新时间，给一个默认时间
            if (alarmTimeMillis >= ALARM_REFRESH_TIME_MILLIS_THRESHOLD) {
                // 缓存过期前 2min 执行自动刷新
                timeout = expireMillis - MIN_REFRESH_TIME_MILLIS;
            } else {
                // 缓存过期前 1min 执行自动刷新
                timeout = expireMillis - MIN_REFRESH_TIME_MILLIS / 2;
            }
        }

        // 上次从 datasource 加载数据的时间 < timeout，则不需要进行刷新
        if ((System.currentTimeMillis() - cacheWrapper.getLastLoadTimeMillis()) < (timeout * 1000)) {
            return false;
        }

        Byte tmpByte = activeRefreshingMap.get(cacheKeyBo);
        // 如果当前 key 有正在刷新的请求，则不处理
        if (null != tmpByte) {
            return false;
        }

        return true;
    }

    /**
     * 刷新缓存
     */
    private void reallyRefresh(ICacheProxy proxy, EzCache ezCache, CacheKeyBo cacheKeyBo, CacheWrapper<Object> cacheWrapper) {
        DataLoader dataLoader = new DataLoader();
        CacheWrapper<Object> newCacheWrapper = null;
        boolean isFirst = false;

        try {
            // 从 datasource 获取数据
            newCacheWrapper = dataLoader.init(proxy, cacheKeyBo, ezCache, cacheProcessor).getData().getCacheWrapper();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        } finally {
            isFirst = dataLoader.isFirst();
        }

        // 只有首次请求才需要真正写入缓存
        if (isFirst) {
            // TODO（这里会出现不一致性问题） 如果数据加载失败，则把旧数据进行续租
            if (null == newCacheWrapper && null != cacheWrapper) {
                long newExpireMillis = cacheWrapper.getExpireMillis() / 2;
                if (newExpireMillis < 2 * 60 * 1000) {
                    newExpireMillis = 2 * 60 * 1000;
                }
                newCacheWrapper = new CacheWrapper<Object>(cacheWrapper.getCacheObject(), newExpireMillis);
            }

            try {
                if (null != newCacheWrapper) {
                    cacheProcessor.reallyWriteCache(proxy, proxy.getArgs(), ezCache, cacheKeyBo, newCacheWrapper);
                }
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            }

            activeRefreshingMap.remove(cacheKeyBo);
        }
    }

    public void shutdown() {
        // 关闭线程池
        activeRefreshThreadPoolExecutor.shutdownNow();
        try {
            activeRefreshThreadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS);
            log.info("ActiveRefreshProcessor shutdown.");
        } catch (InterruptedException e) {
            log.error("ActiveRefreshProcessor shutdown failed.", e);
        }
    }

    class AsyncRefreshTask implements Runnable {

        private final ICacheProxy proxy;

        private final EzCache ezCache;

        private final CacheKeyBo cacheKeyBo;

        private final CacheWrapper<Object> cacheWrapper;

        public AsyncRefreshTask(ICacheProxy proxy, EzCache ezCache, CacheKeyBo cacheKeyBo, CacheWrapper<Object> cacheWrapper) {
            this.proxy = proxy;
            this.ezCache = ezCache;
            this.cacheKeyBo = cacheKeyBo;
            this.cacheWrapper = cacheWrapper;
        }

        public CacheKeyBo getCacheKeyBo() {
            return cacheKeyBo;
        }

        @Override
        public void run() {
            reallyRefresh(proxy, ezCache, cacheKeyBo, cacheWrapper);
        }
    }
}
