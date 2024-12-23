package cn.itmtx.ezcache.core.autorefresh;

import cn.itmtx.ezcache.common.annotation.EzCache;
import cn.itmtx.ezcache.common.constant.CommonConstant;
import cn.itmtx.ezcache.core.CacheProcessor;
import cn.itmtx.ezcache.common.bo.CacheKeyBo;
import cn.itmtx.ezcache.common.bo.CacheWrapper;
import cn.itmtx.ezcache.common.bo.EzCacheConfig;
import cn.itmtx.ezcache.core.activerefresh.ActiveRefreshProcessor;
import cn.itmtx.ezcache.core.datasource.DataLoader;
import cn.itmtx.ezcache.core.proxy.ICacheProxy;
import cn.itmtx.ezcache.core.utils.RefreshUtils;
import cn.itmtx.ezcache.parser.IExpressionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 自动刷新缓存处理器
 */
public class AutoRefreshProcessor {

    private static final Logger log = LoggerFactory.getLogger(AutoRefreshProcessor.class);

    private static final String THREAD_NAME_PREFIX = "EzCache-AutoRefreshThread-";

    private final CacheProcessor cacheProcessor;

    private final EzCacheConfig ezCacheConfig;

    private final IExpressionParser expressionParser;

    /**
     * 自动刷新 Map
     */
    private final ConcurrentHashMap<CacheKeyBo, AutoRefreshBo> autoRefreshMap;

    /**
     * 自动刷新有序队列
     */
    private final LinkedBlockingQueue<AutoRefreshBo> autoRefreshQueue;

    /**
     * 对自动加载队列进行排序的线程
     */
    private final Thread sortThread;

    /**
     * 处理自动刷新队列的线程池
     */
    private final Thread[] workThreads;

    /**
     * 自动刷新缓存处理器是否正在运行
     */
    private volatile boolean running = false;

    public AutoRefreshProcessor(CacheProcessor cacheProcessor) {
        this.cacheProcessor = cacheProcessor;
        this.ezCacheConfig = cacheProcessor.getEzCacheConfig();
        this.expressionParser = cacheProcessor.getExpressionParser();

        if (this.ezCacheConfig.getAutoRefreshThreadCount() > 0) {
            this.running = true;

            this.autoRefreshMap = new ConcurrentHashMap<>(this.ezCacheConfig.getAutoRefreshMaxQueueCapacity());
            this.autoRefreshQueue = new LinkedBlockingQueue<>(this.ezCacheConfig.getAutoRefreshMaxQueueCapacity());
            this.workThreads = new Thread[this.ezCacheConfig.getAutoRefreshThreadCount()];

            // 开启排序线程
            this.sortThread = new Thread(new SortRunnable());
            this.sortThread.setDaemon(true);
            this.sortThread.start();

            // 开启刷新线程
            for (int i = 0; i < this.ezCacheConfig.getAutoRefreshThreadCount(); i ++) {
                this.workThreads[i] = new Thread(new AutoRefreshRunnable());
                this.workThreads[i].setName(THREAD_NAME_PREFIX + i);
                this.workThreads[i].setDaemon(true);
                this.workThreads[i].start();
            }
        } else {
            this.running = false;

            this.autoRefreshMap = null;
            this.autoRefreshQueue = null;
            this.workThreads = null;
            this.sortThread = null;
        }
    }

    /**
     * 获取自动刷新相关信息
     * @param cacheKeyBo
     * @return
     */
    public AutoRefreshBo getAutoRefreshBo(CacheKeyBo cacheKeyBo) {
        if (null == autoRefreshMap) {
            return null;
        }
        return autoRefreshMap.get(cacheKeyBo);
    }

    /**
     * 往自动刷新队列中添加任务
     * @param cacheKeyBo
     * @param proxy
     * @param ezCache
     * @param cacheWrapper
     * @return
     */
    public AutoRefreshBo addRefreshTask(CacheKeyBo cacheKeyBo, ICacheProxy proxy, EzCache ezCache,
                                        CacheWrapper<Object> cacheWrapper) {
        if (null == autoRefreshMap) {
            return null;
        }

        // 若任务已存在则直接返回
        AutoRefreshBo autoRefreshBo = autoRefreshMap.get(cacheKeyBo);
        if (null != autoRefreshBo) {
            return autoRefreshBo;
        }

        try {
            boolean autoRefreshable = this.expressionParser.isAutoRefreshable(ezCache, proxy.getTarget(), proxy.getArgs(), cacheWrapper.getCacheObject());
            if (!autoRefreshable) {
                log.info("not open auto refresh.");
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
        log.info("open auto refresh.");

        // 往队列中添加任务
        long expireMillis = cacheWrapper.getExpireMillis();
        if (expireMillis > 0 && autoRefreshMap.size() <= this.ezCacheConfig.getAutoRefreshMaxQueueCapacity()) {
            autoRefreshBo = new AutoRefreshBo(proxy, proxy.getArgs(), cacheKeyBo, ezCache, expireMillis);
            // putIfAbsent(key,value): key 存在返回对应的 value, key 不存在返回 null
            AutoRefreshBo temp = autoRefreshMap.putIfAbsent(cacheKeyBo, autoRefreshBo);
            if (Objects.isNull(temp)) {
                return autoRefreshBo;
            } else {
                return temp;
            }
        }

        return null;
    }

    public void shutdown() {
        this.running = false;

        if (null != autoRefreshMap) {
            autoRefreshMap.clear();
        }
        if (null != autoRefreshQueue) {
            autoRefreshQueue.clear();
        }
        log.info("AutoRefreshProcessor shutdown.");
    }

    /**
     * 获取自动加载队列中的所有任务（排序）
     * @return
     */
    public AutoRefreshBo[] getAllAutoRefreshBos() {
        if (null == autoRefreshMap || autoRefreshMap.isEmpty()) {
            return null;
        }
        AutoRefreshBo[] autoRefreshBos = new AutoRefreshBo[autoRefreshMap.size()];
        autoRefreshBos = autoRefreshMap.values().toArray(autoRefreshBos);
        if (ezCacheConfig.getAutoRefreshQueueSortType() != AutoRefreshQueueSortTypeEnum.NONE.getSortId()) {
            AutoRefreshQueueSortTypeEnum autoRefreshQueueSortTypeEnum = AutoRefreshQueueSortTypeEnum.findBy(ezCacheConfig.getAutoRefreshQueueSortType());
            Comparator<AutoRefreshBo> comparator = autoRefreshQueueSortTypeEnum.getComparator();
            if (Objects.nonNull(comparator)) {
                Arrays.sort(autoRefreshBos, comparator);
            }
        }
        return autoRefreshBos;
    }

    /**
     * 删除自动刷新任务
     * @param cacheKeyBo
     */
    public void removeAutoRefreshBo(CacheKeyBo cacheKeyBo) {
        if (null == autoRefreshMap) {
            return ;
        }

        autoRefreshMap.remove(cacheKeyBo);
    }

    /**
     * 将任务排序，并放进队列中
     */
    private class SortRunnable implements Runnable {
        @Override
        public void run() {
            while (running) {
                int sleepMillis = 100;
                // 如果没有数据 or 还有线程在处理，则继续等待
                if (autoRefreshMap.isEmpty() || autoRefreshQueue.size() > 0) {
                    try {
                        Thread.sleep(sleepMillis);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                    }
                    continue;
                } else if (autoRefreshMap.size() <= workThreads.length * 10) {
                    sleepMillis = 1000;
                } else if (autoRefreshMap.size() <= workThreads.length * 50) {
                    sleepMillis = 300;
                }
                try {
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }

                // 获取排序好的所有任务
                AutoRefreshBo[] autoRefreshBos = getAllAutoRefreshBos();
                if (null == autoRefreshBos || autoRefreshBos.length == 0) {
                    continue;
                }
                for (int i = 0; i < autoRefreshBos.length; i++) {
                    try {
                        AutoRefreshBo bo = autoRefreshBos[i];
                        autoRefreshQueue.put(bo);
                        if (i > 0 && i % 1000 == 0) {
                            // TODO 让其它线程获得CPU控制权的权力
                            Thread.yield();
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * 缓存刷新线程
     */
    private class AutoRefreshRunnable implements Runnable {
        @Override
        public void run() {
            while (running) {
                try {
                    if (null != autoRefreshQueue) {
                        AutoRefreshBo bo = autoRefreshQueue.take();
                        reallyRefresh(bo);
                    }
                    Thread.sleep(100);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        /**
         * 刷新缓存
         * @param autoRefreshBo
         */
        private void reallyRefresh(AutoRefreshBo autoRefreshBo) {
            if (null == autoRefreshBo) {
                return;
            }
            long now = System.currentTimeMillis();
            if (autoRefreshBo.getLastRequestTimeMillis() <= 0 || autoRefreshBo.getLastLoadTimeMillis() <= 0) {
                return;
            }
            EzCache ezCache = autoRefreshBo.getCache();
            CacheKeyBo cacheKeyBo = autoRefreshBo.getCacheKey();
            // 缓存数据持续 autoRefreshNoRequestTimeoutMillis(单位：毫秒) 没有被使用，就关闭对此缓存数据的自动刷新(从队列中删除)
            long noRequestTimeoutMillis = ezCache.autoRefreshNoRequestTimeoutMillis();
            if (noRequestTimeoutMillis > 0 && (now - autoRefreshBo.getLastRequestTimeMillis()) >= noRequestTimeoutMillis) {
                Optional.ofNullable(autoRefreshMap).ifPresent(map -> autoRefreshMap.remove(cacheKeyBo));
                return;
            }

            // TODO 如果是耗时短且使用率低的数据，可以考虑不使用自动加载

            // 判断是否允许刷新
            long expireTimeMillis = autoRefreshBo.getExpireTimeMils();
            long lastLoadTimeMillis = autoRefreshBo.getLastLoadTimeMillis();
            RefreshUtils.ExecuteRefreshBo executeRefreshBo = RefreshUtils.isAllowRefresh(ezCache, expireTimeMillis, lastLoadTimeMillis);
            boolean executeRefresh = executeRefreshBo.getAllowRefresh();
            if (!executeRefresh) {
                return ;
            }

            // 从 datasource 获取数据之前去缓存服务器中检查，数据是否已经被别的服务器更新了，如果是，则不需要再次更新
            CacheWrapper<Object> result = null;
            try {
                result = cacheProcessor.getCacheOperator().getCache(autoRefreshBo.getCacheKey());
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
            // 如果已经被别的服务器更新了，则不需要再次更新
            if (null != result) {
                autoRefreshBo.setExpireTimeMils(result.getExpireMillis());
                if (result.getLastLoadTimeMillis() > autoRefreshBo.getLastLoadTimeMillis()
                        && (System.currentTimeMillis() - result.getLastLoadTimeMillis()) < executeRefreshBo.getRefreshMillis()) {
                    autoRefreshBo.setLastLoadTimeMillis(result.getLastLoadTimeMillis());
                    return ;
                }
            }

            // 从 datasource 获取数据
            ICacheProxy proxy = autoRefreshBo.getProxy();
            DataLoader dataLoader = new DataLoader();
            CacheWrapper<Object> newCacheWrapper = null;
            boolean isFirst = false;
            // 从 datasource 加载数据消费的时间
            long loadDataUseTime = 0L;
            try {
                newCacheWrapper = dataLoader.init(proxy, autoRefreshBo, cacheKeyBo, ezCache, cacheProcessor).getData().getCacheWrapper();
                loadDataUseTime = dataLoader.getLoadDataTimeMills();
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            } finally {
                isFirst = dataLoader.isFirst();
            }

            // 只有首次请求才需要真正写入缓存
            if (isFirst) {
                // TODO（这里会出现不一致性问题） 如果从 datasource 加载数据失败，则把旧数据续租 3min
                if (null == newCacheWrapper && null != result) {
                    long newExpireMillis = RefreshUtils.MIN_REFRESH_TIME_MILLIS + 60 * CommonConstant.ONE_THOUSAND_MILLIS;
                    newCacheWrapper = new CacheWrapper<Object>(result.getCacheObject(), newExpireMillis);
                }

                try {
                    if (Objects.nonNull(newCacheWrapper)) {
                        // 写入缓存
                        cacheProcessor.reallyWriteCache(proxy, autoRefreshBo.getArgs(), ezCache, cacheKeyBo, newCacheWrapper);
                        // 更新刷新信息戳
                        autoRefreshBo.setLastLoadTimeMillis(newCacheWrapper.getLastLoadTimeMillis())
                                .setExpireTimeMils(newCacheWrapper.getExpireMillis())
                                .addTotalLoadDataTime(loadDataUseTime);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}
