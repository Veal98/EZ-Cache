package cn.itmtx.ezcache;

import cn.itmtx.ezcache.annotation.EzCache;
import cn.itmtx.ezcache.bo.AutoRefreshBo;
import cn.itmtx.ezcache.bo.CacheKeyBo;
import cn.itmtx.ezcache.bo.CacheWrapper;
import cn.itmtx.ezcache.config.AutoLoadConfig;
import cn.itmtx.ezcache.proxy.ICacheProxy;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 自动刷新缓存处理器
 */
public class AutoRefreshProcessor {

    private final CacheProcessor cacheProcessor;

    private final AutoLoadConfig autoLoadConfig;

    /**
     * 自动加载 Map
     */
    private final ConcurrentHashMap<CacheKeyBo, AutoRefreshBo> autoLoadMap;

    /**
     * 自动加载有序队列
     */
    private final LinkedBlockingQueue<AutoRefreshBo> autoLoadQueue;


    /**
     * 处理自动加载队列的线程池
     */
    private final Thread[] threads;

    public AutoRefreshProcessor(CacheProcessor cacheProcessor, AutoLoadConfig autoLoadConfig) {
        this.cacheProcessor = cacheProcessor;
        this.autoLoadConfig = autoLoadConfig;

        if (this.autoLoadConfig.getThreadCnt() > 0) {
            this.threads = new Thread[this.autoLoadConfig.getThreadCnt()];
            this.autoLoadMap = new ConcurrentHashMap<>(this.autoLoadConfig.getMaxElementSize());
            this.autoLoadQueue = new LinkedBlockingQueue<>(this.autoLoadConfig.getMaxElementSize());
        } else {
            this.threads = null;
            this.autoLoadMap = null;
            this.autoLoadQueue = null;
        }
    }

    /**
     * 获取自动加载的缓存相关信息
     * @param cacheKeyBo
     * @return
     */
    public AutoRefreshBo getAutoLoadTO(CacheKeyBo cacheKeyBo) {
        return null;
    }

    /**
     * 往自动加载队列中添加任务
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
