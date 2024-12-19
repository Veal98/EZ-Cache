package cn.itmtx.ezcache.core;

import cn.itmtx.ezcache.common.annotation.EzCache;
import cn.itmtx.ezcache.core.bo.AutoRefreshBo;
import cn.itmtx.ezcache.common.bo.CacheKeyBo;
import cn.itmtx.ezcache.common.bo.CacheWrapper;
import cn.itmtx.ezcache.core.proxy.ICacheProxy;
import cn.itmtx.ezcache.common.bo.EzCacheConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 自动刷新缓存处理器
 */
public class AutoRefreshProcessor {

    private static final Logger log = LoggerFactory.getLogger(AutoRefreshProcessor.class);

    private final CacheProcessor cacheProcessor;

    private final EzCacheConfig ezCacheConfig;

    /**
     * 自动刷新 Map
     */
    private final ConcurrentHashMap<CacheKeyBo, AutoRefreshBo> autoRefreshMap;

    /**
     * 自动刷新有序队列
     */
    private final LinkedBlockingQueue<AutoRefreshBo> autoRefreshQueue;


    /**
     * 处理自动刷新队列的线程池
     */
    private final Thread[] threads;

    /**
     * 自动刷新缓存处理器是否正在运行
     */
    private volatile boolean running = false;

    public AutoRefreshProcessor(CacheProcessor cacheProcessor, EzCacheConfig ezCacheConfig) {
        this.cacheProcessor = cacheProcessor;
        this.ezCacheConfig = ezCacheConfig;

        if (this.ezCacheConfig.getThreadCnt() > 0) {
            this.threads = new Thread[this.ezCacheConfig.getThreadCnt()];
            this.autoRefreshMap = new ConcurrentHashMap<>(this.ezCacheConfig.getMaxElementSize());
            this.autoRefreshQueue = new LinkedBlockingQueue<>(this.ezCacheConfig.getMaxElementSize());
        } else {
            this.threads = null;
            this.autoRefreshMap = null;
            this.autoRefreshQueue = null;
        }
    }

    /**
     * 获取自动刷新的缓存相关信息
     * @param cacheKeyBo
     * @return
     */
    public AutoRefreshBo getAutoRefreshBo(CacheKeyBo cacheKeyBo) {
        return null;
    }

    /**
     * 往自动刷新队列中添加任务
     * @param cacheKeyBo
     * @param proxy
     * @param ezCache
     * @param cacheWrapper
     * @return
     */
    public AutoRefreshBo putIfAbsent(CacheKeyBo cacheKeyBo, ICacheProxy proxy, EzCache ezCache,
                                     CacheWrapper<Object> cacheWrapper) {
        return null;
    }

    public void shutdown() {
        running = false;
        if (null != autoRefreshMap) {
            autoRefreshMap.clear();
        }
        if (null != autoRefreshQueue) {
            autoRefreshQueue.clear();
        }
        log.info("AutoRefreshProcessor shutdown.");
    }
}
