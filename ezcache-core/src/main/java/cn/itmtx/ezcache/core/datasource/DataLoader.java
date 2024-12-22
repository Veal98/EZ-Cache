package cn.itmtx.ezcache.core.datasource;

import cn.itmtx.ezcache.common.annotation.EzCache;
import cn.itmtx.ezcache.core.CacheProcessor;
import cn.itmtx.ezcache.core.proxy.ICacheProxy;
import cn.itmtx.ezcache.lock.IDistributedLock;
import cn.itmtx.ezcache.core.autorefresh.AutoRefreshBo;
import cn.itmtx.ezcache.common.bo.CacheKeyBo;
import cn.itmtx.ezcache.common.bo.CacheWrapper;
import cn.itmtx.ezcache.lock.enums.LockStateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private CacheProcessor cacheProcessor;

    private ICacheProxy proxy;

    private CacheKeyBo cacheKeyBo;

    private EzCache ezCache;

    private Object[] args;

    private AutoRefreshBo autoRefreshBo;

    /**
     * 是否是第一个 datasource 请求
     */
    private boolean isFirst;

    /**
     * 从 datasource 加载数据所花费的时间
     */
    private long loadDataTimeMills;

    private CacheWrapper<Object> cacheWrapper;

    /**
     * 从 datasource 中获取数据的重试次数
     */
    private int tryGetDataCount;

    public DataLoader() {
    }

    public DataLoader init(ICacheProxy proxy, EzCache ezCache, CacheProcessor cacheProcessor) {
        return init(proxy, null, null, ezCache, cacheProcessor);
    }

    public DataLoader init(ICacheProxy proxy, AutoRefreshBo autoRefreshBo, CacheKeyBo cacheKeyBo, EzCache ezCache, CacheProcessor cacheProcessor) {
        this.cacheProcessor = cacheProcessor;
        this.proxy = proxy;
        this.cacheKeyBo = cacheKeyBo;
        this.ezCache = ezCache;
        this.autoRefreshBo = autoRefreshBo;

        if (null == autoRefreshBo) {
            // 用户请求
            this.args = proxy.getArgs();
        } else {
            // 来自 AutoLoadProcessor 的请求
            this.args = autoRefreshBo.getArgs();
        }
        this.loadDataTimeMills = 0;
        this.tryGetDataCount = 0;
        return this;
    }

    public DataLoader init(ICacheProxy proxy, CacheKeyBo cacheKey, EzCache ezCache, CacheProcessor cacheProcessor) {
        return init(proxy, null, cacheKey, ezCache, cacheProcessor);
    }

    /**
     * 重置数据
     */
    public void reset() {
    }

    /**
     * 处理从 datasource 中获取数据的并发控制和请求等待逻辑
     * 简单理解：每个机器上对 datasource 的并发请求选举出一个 leader，由这些每个机器的 leader 去进行分布式锁的争抢
     * 争抢到分布式锁的 leader 获取 datasource 数据后，将其写入分布式缓存; 其他没有争抢到分布式锁的 leader 一直尝试去读取分布式缓存的数据
     * 非 leader 请求等待各自机器的 leader 获取到数据
     * @return
     * @throws Throwable
     */
    public DataLoader getData() throws Throwable {
        // 1. 根据 cache key 查找是否有正在处理中的 datasource 请求
        ProcessingBo processingBo = cacheProcessor.datasourceProcessingMap.get(cacheKeyBo);

        // 2. 没有正在处理中的 datasource 请求
        if (Objects.isNull(processingBo)) {
            ProcessingBo newProcessingBo = new ProcessingBo();
            // putIfAbsent(key,value): key 存在返回对应的 value, key 不存在返回 null
            ProcessingBo firstProcessingBo = cacheProcessor.datasourceProcessingMap.putIfAbsent(cacheKeyBo, newProcessingBo);
            if (Objects.isNull(firstProcessingBo)) {
                // 当前 datasource 并发中的第一个请求
                isFirst = true;
                processingBo = newProcessingBo;
            } else {
                // 不是当前 datasource 并发中的第一个请求
                isFirst = false;
                // 获取到第一个线程的 ProcessingBo 的引用，保证当前机器的所有请求都指向同一个引用
                processingBo = firstProcessingBo;
            }
        } else {
            // 已经有正在处理中的请求了
            isFirst = false;
        }

        // 3. 有正在处理中的 datasource 请求

        // 锁住当前机器的 datasource 请求, 防止并发修改
        Object lock = processingBo;
        String threadName = Thread.currentThread().getName();
        if (isFirst) {
            log.info("{} first thread!", threadName);
            try {
                getFirstRequestData(processingBo);
            } catch (Exception e) {
                processingBo.setError(e);
                throw e;
            } finally {
                processingBo.setFirstFinished(true);
                // 去除 processingMap 中已经处理完的 processingBo
                cacheProcessor.datasourceProcessingMap.remove(cacheKeyBo);
                synchronized (lock) {
                    // 释放锁
                    lock.notifyAll();
                }
            }
        } else {
            waitFirstRequestData(processingBo, lock);
        }

        return this;
    }

    /**
     * 从 datasource 中获取数据
     * @return
     * @throws Throwable
     */
    public DataLoader reallyGetData() throws Throwable{
        try {
            if (null != autoRefreshBo) {
                autoRefreshBo.setLoading(true);
            }

            // 从 datasource 中获取数据
            Object result = proxy.doProxy(args);

            long expireTimeMillis = ezCache.expireTimeMillis();
            cacheWrapper = new CacheWrapper<>(result, expireTimeMillis);
        } catch (Throwable e) {
            throw e;
        } finally {
            if (null != autoRefreshBo) {
                autoRefreshBo.setLoading(false);
            }
        }
        return this;
    }

    /**
     * 当前 processingBo 是并发请求中的第一个，需要从 datasource 中获取数据
     * @param processingBo
     */
    private void getFirstRequestData(ProcessingBo processingBo) throws Throwable {
        IDistributedLock distributedLock = cacheProcessor.getDistributedLock();
        if (null != distributedLock && ezCache.distributedLockTimeoutMillis() > 0) {
            String lockKey = cacheKeyBo.getLockKey();
            // 当前线程首次请求分布式锁的时间（或者说是其他线程首次等待分布式锁的时间）
            long startWaitLockTime = processingBo.getStartTime();
            LockStateEnum lockState = tryDistributedLock(distributedLock, lockKey);
            if (LockStateEnum.LOCKED.equals(lockState)) {
                // 成功获取到锁
                try {
                    // 从 datasource 获取数据
                    this.reallyGetData();
                } finally {
                    // 释放分布式锁
                    try {
                        distributedLock.unlock(lockKey);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            } else if (LockStateEnum.UN_LOCKED.equals(lockState)) {
                // 未获取到锁。在 waitDatasourceTimeoutMillis 时间内，一直尝试从缓存中获取数据（其实就是：等待获取到分布式锁的那个机器的请求把从 datasource 中获取到的数据写入缓存）
                do {
                    cacheWrapper = cacheProcessor.getCacheWrapper(cacheKeyBo);
                    if (null != cacheWrapper) {
                        break;
                    }
                    Thread.sleep(10);
                } while (System.currentTimeMillis() - startWaitLockTime < ezCache.waitDatasourceTimeoutMillis());
            }
        }

        if (null == cacheWrapper) {
            // 走到这里有两种可能性，分布式锁异常 LockStateEnum.Exception 或者上一步没有从 datasource 中获取到数据
            this.reallyGetData();
        }

        // 从 datasource 中获取到数据后，将其存入本地缓存
        processingBo.setCacheWrapper(cacheWrapper);
    }

    /**
     * 当前 processingBo 不是并发请求中的第一个，需要等待第一个请求返回结果
     * @param processingBo
     * @param lock
     */
    private void waitFirstRequestData(ProcessingBo processingBo, Object lock) throws Throwable {
        if (Objects.isNull(processingBo)) {
            return ;
        }

        long startWaitLockTime = processingBo.getStartTime();
        String threadName = Thread.currentThread().getName();
        do {
            // 第一个请求结束
            if (processingBo.isFirstFinished()) {
                // 优先从本地内存获取数据，防止频繁去缓存服务器取数据，造成缓存服务器压力过大
                CacheWrapper<Object> tempCacheWrapper = processingBo.getCacheWrapper();
                log.info("{} do FirstFinished, cache is null :{}", threadName, (null == tempCacheWrapper));
                if (null != tempCacheWrapper) {
                    cacheWrapper = tempCacheWrapper;
                    return ;
                }

                Throwable error = processingBo.getError();
                if (null != error) {
                    log.info("{} do error", threadName);
                    throw error;
                }
                break;
            } else {
                // 第一个请求未结束，则等待第一个请求释放锁
                synchronized (lock) {
                    log.trace("{} do wait", threadName);
                    try {
                        lock.wait(2);
                    } catch (InterruptedException ex) {
                        log.error(ex.getMessage(), ex);
                    }
                }
            }
        } while (System.currentTimeMillis() - startWaitLockTime < ezCache.waitDatasourceTimeoutMillis());

        // 若走到这 cacheWrapper == null，则尝试从缓存服务器中获取数据
        if (null == cacheWrapper) {
            cacheWrapper = cacheProcessor.getCacheWrapper(cacheKeyBo);
        }

        // 若本地内存、缓存服务器中的数据都为空，说明从 datasource 获取数据失败，进行重试
        if (null == cacheWrapper) {
            if (tryGetDataCount <= 3) {
                tryGetDataCount ++;
                getData();
            } else {
                throw new Exception("cache for key \"" + cacheKeyBo.getCacheKey() + "\" loaded " + tryGetDataCount + " times.");
            }
        }
    }

    private LockStateEnum tryDistributedLock(IDistributedLock distributedLock, String lockKey) {
        try {
            return distributedLock.tryLock(lockKey, ezCache.distributedLockTimeoutMillis()) ? LockStateEnum.LOCKED : LockStateEnum.UN_LOCKED;
        } catch (Throwable e) {
            // 获取分布式锁失败，并且打开了锁降级
            if (ezCache.openDistributedLockDown()) {
                // 关闭分布式锁
                cacheProcessor.setDistributedLock(null);
                log.error("分布式锁异常，强制停止使用分布式锁!", e);
            } else {
                log.error("分布式锁异常!", e);
            }
            return LockStateEnum.EXCEPTION;
        }
    }

    public boolean isFirst() {
        return isFirst;
    }

    public CacheWrapper<Object> getCacheWrapper() {
        return cacheWrapper;
    }

    public long getLoadDataTimeMills() {
        return loadDataTimeMills;
    }

}
