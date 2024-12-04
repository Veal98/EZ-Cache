package cn.itmtx.ezcache.bo;

import java.io.Serializable;

public class CacheWrapper<T> implements Serializable, Cloneable {

    /**
     * 缓存数据
     */
    private T cacheObject;

    /**
     * 最近 load 时间
     */
    private long lastLoadTimeMillis;

    /**
     * 缓存过期时长(单位:毫秒)
     */
    private long expireMillis;

    public boolean isExpired() {
        if (expireMillis > 0) {
            // 假设 expireMillis = 3 天，如果这个缓存数据 3 天没有被使用过，则表示这个缓存国企
            return (System.currentTimeMillis() - lastLoadTimeMillis) > expireMillis;
        }
        return false;
    }

    public T getCacheObject() {
        return cacheObject;
    }

    public void setCacheObject(T cacheObject) {
        this.cacheObject = cacheObject;
    }

    public long getLastLoadTimeMillis() {
        return lastLoadTimeMillis;
    }

    public void setLastLoadTimeMillis(long lastLoadTimeMillis) {
        this.lastLoadTimeMillis = lastLoadTimeMillis;
    }

    public long getExpireMillis() {
        return expireMillis;
    }

    public void setExpireMillis(long expireMillis) {
        this.expireMillis = expireMillis;
    }
}
