package cn.itmtx.ezcache.core.autorefresh;

import cn.itmtx.ezcache.common.annotation.EzCache;
import cn.itmtx.ezcache.common.bo.CacheKeyBo;
import cn.itmtx.ezcache.common.bo.CacheWrapper;
import cn.itmtx.ezcache.core.proxy.ICacheProxy;

import java.io.Serializable;

/**
 * 用于处理自动刷新数据到缓存
 */
public class AutoRefreshBo implements Serializable {

    private final ICacheProxy proxy;

    private final Object[] args;

    /**
     * 缓存Key
     */
    private final CacheKeyBo cacheKey;

    /**
     * 缓存注解
     */
    private final EzCache cache;

    /**
     * 缓存过期时长
     */
    private long expireTimeMils;

    /**
     * 上次请求数据时间
     */
    private long lastRequestTimeMillis = 0L;

    /**
     * 第一次请求数据时间
     */
    private long firstRequestTimeMillis = 0L;

    /**
     * 请求数据次数
     */
    private long requestCount = 0L;

    /**
     * 是否正在请求 datasource 数据中, 若是的话则不要执行自动刷新
     */
    private volatile boolean loading = false;

    /**
     * 从 datasource 加载数据的次数
     */
    private long loadCount = 0L;

    /**
     * 上次从 datasource 加载数据的时间
     */
    private long lastLoadTimeMillis = 0L;

    /**
     * 从 datasource 中加载数据所耗费的时间总和
     */
    private long loadDataTotalTimeMillis = 0L;

    public AutoRefreshBo(ICacheProxy proxy, Object[] args, CacheKeyBo cacheKey, EzCache cache, long expireTimeMils) {
        this.proxy = proxy;
        this.args = args;
        this.cacheKey = cacheKey;
        this.cache = cache;
        this.expireTimeMils = expireTimeMils;
    }

    public void flushRequestTime(CacheWrapper<Object> cacheWrapper) {
        // 同步最后加载时间
        this.setLastRequestTime(System.currentTimeMillis())
                // 同步加载时间
                .setLastLoadTimeMillis(cacheWrapper.getLastLoadTimeMillis())
                // 同步过期时间
                .setExpireTimeMils(cacheWrapper.getExpireMillis());
    }

    /**
     * 记录 load 用时
     *
     * @param loadTimeMillis 用时
     * @return this
     */
    public AutoRefreshBo addTotalLoadDataTime(long loadTimeMillis) {
        synchronized (this) {
            this.loadCount ++;
            this.loadDataTotalTimeMillis += loadTimeMillis;
        }
        return this;
    }

    /**
     * load 平均耗时
     *
     * @return long 平均耗时
     */
    public long getAvgLoadDataTime() {
        if (loadCount == 0) {
            return 0;
        }
        return this.loadDataTotalTimeMillis / this.loadCount;
    }

    public ICacheProxy getProxy() {
        return proxy;
    }

    public Object[] getArgs() {
        return args;
    }

    public CacheKeyBo getCacheKey() {
        return cacheKey;
    }

    public EzCache getCache() {
        return cache;
    }

    public long getExpireTimeMils() {
        return expireTimeMils;
    }

    public AutoRefreshBo setExpireTimeMils(long expireTimeMils) {
        this.expireTimeMils = expireTimeMils;
        return this;
    }

    public long getLastRequestTimeMillis() {
        return lastRequestTimeMillis;
    }

    /**
     * 更新最近请求数据时间
     * @param lastRequestTimeMillis
     * @return
     */
    public AutoRefreshBo setLastRequestTime(long lastRequestTimeMillis) {
        synchronized (this) {
            this.lastRequestTimeMillis = lastRequestTimeMillis;
            if (firstRequestTimeMillis == 0) {
                firstRequestTimeMillis = lastRequestTimeMillis;
            }
            requestCount ++;
        }
        return this;
    }

    public long getFirstRequestTimeMillis() {
        return firstRequestTimeMillis;
    }

    public void setFirstRequestTimeMillis(long firstRequestTimeMillis) {
        this.firstRequestTimeMillis = firstRequestTimeMillis;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(long requestCount) {
        this.requestCount = requestCount;
    }

    public boolean isLoading() {
        return loading;
    }

    public AutoRefreshBo setLoading(boolean loading) {
        this.loading = loading;
        return this;
    }

    public long getLoadCount() {
        return loadCount;
    }

    public void setLoadCount(long loadCount) {
        this.loadCount = loadCount;
    }

    public long getLastLoadTimeMillis() {
        return lastLoadTimeMillis;
    }

    public AutoRefreshBo setLastLoadTimeMillis(long lastLoadTimeMillis) {
        this.lastLoadTimeMillis = lastLoadTimeMillis;
        return this;
    }

    public long getLoadDataTotalTimeMillis() {
        return loadDataTotalTimeMillis;
    }

    public void setLoadDataTotalTimeMillis(long loadDataTotalTimeMillis) {
        this.loadDataTotalTimeMillis = loadDataTotalTimeMillis;
    }
}
