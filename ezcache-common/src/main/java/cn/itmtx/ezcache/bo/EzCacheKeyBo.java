package cn.itmtx.ezcache.bo;

import java.io.Serializable;

/**
 * business object for cache key
 */
public class EzCacheKeyBo  implements Serializable {

    /**
     * cache namespace
     */
    private String namespace;

    /**
     * cache key
     */
    private String key;

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
