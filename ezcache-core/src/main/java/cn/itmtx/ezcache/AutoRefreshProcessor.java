package cn.itmtx.ezcache;

import cn.itmtx.ezcache.annotation.EzCache;
import cn.itmtx.ezcache.bo.AutoRefreshBo;
import cn.itmtx.ezcache.bo.CacheKeyBo;
import cn.itmtx.ezcache.bo.CacheWrapper;
import cn.itmtx.ezcache.bo.CacheConfigBo;
import cn.itmtx.ezcache.proxy.ICacheProxy;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 自动刷新缓存处理器
 */
public class AutoRefreshProcessor {

    private final CacheProcessor cacheProcessor;

    private final CacheConfigBo cacheConfigBo;

    /**
     * 自动刷新 Map
     */
    private final ConcurrentHashMap<CacheKeyBo, AutoRefreshBo> autoLoadMap;

    /**
     * 自动刷新有序队列
     */
    private final LinkedBlockingQueue<AutoRefreshBo> autoLoadQueue;


    /**
     * 处理自动刷新队列的线程池
     */
    private final Thread[] threads;

    public AutoRefreshProcessor(CacheProcessor cacheProcessor, CacheConfigBo cacheConfigBo) {
        this.cacheProcessor = cacheProcessor;
        this.cacheConfigBo = cacheConfigBo;

        if (this.cacheConfigBo.getThreadCnt() > 0) {
            this.threads = new Thread[this.cacheConfigBo.getThreadCnt()];
            this.autoLoadMap = new ConcurrentHashMap<>(this.cacheConfigBo.getMaxElementSize());
            this.autoLoadQueue = new LinkedBlockingQueue<>(this.cacheConfigBo.getMaxElementSize());
        } else {
            this.threads = null;
            this.autoLoadMap = null;
            this.autoLoadQueue = null;
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
}
