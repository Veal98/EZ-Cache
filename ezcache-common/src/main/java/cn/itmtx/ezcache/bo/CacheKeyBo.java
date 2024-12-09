package cn.itmtx.ezcache.bo;

import java.io.Serializable;

/**
 * business object for cache key
 */
public class CacheKeyBo implements Serializable {

    /**
     * cache namespace
     */
    private String namespace;

    /**
     * cache key
     */
    private String key;

    public CacheKeyBo() {
    }

    public CacheKeyBo(String namespace, String key) {
        this.namespace = namespace;
        this.key = key;
    }


    /**
     * build cache key
     * @return
     */
    public String getCacheKey() {
        if (null != this.namespace && this.namespace.length() > 0) {
            return this.namespace + ":" + this.key;
        }
        return this.key;
    }

    /**
     * build lock key
     * @return
     */
    public String getLockKey() {
        return this.getCacheKey() + ":lock";
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
