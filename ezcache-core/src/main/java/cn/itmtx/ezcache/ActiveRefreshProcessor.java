package cn.itmtx.ezcache;

import cn.itmtx.ezcache.annotation.EzCache;
import cn.itmtx.ezcache.bo.CacheKeyBo;
import cn.itmtx.ezcache.bo.CacheWrapper;
import cn.itmtx.ezcache.proxy.ICacheProxy;

/**
 * 主动刷新缓存处理器
 */
public class ActiveRefreshProcessor {

    /**
     * 判断缓存是否快要过期，若快过期则刷新缓存(异步刷新)
     * @param proxy
     * @param cache
     * @param cacheKeyBo
     * @param cacheWrapper
     */
    public void asyncRefresh(ICacheProxy proxy, EzCache cache, CacheKeyBo cacheKeyBo, CacheWrapper<Object> cacheWrapper) {

    }

    /**
     * 判断缓存是否快要过期，若快过期则刷新缓存(同步刷新)
     * @param proxy
     * @param cache
     * @param cacheKeyBo
     * @param cacheWrapper
     */
    public void refresh(ICacheProxy proxy, EzCache cache, CacheKeyBo cacheKeyBo, CacheWrapper<Object> cacheWrapper) {

    }
}
