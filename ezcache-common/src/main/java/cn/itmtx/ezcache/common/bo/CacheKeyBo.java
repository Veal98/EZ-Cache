package cn.itmtx.ezcache.common.bo;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CacheKeyBo that = (CacheKeyBo) o;
        return Objects.equals(namespace, that.namespace) && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, key);
    }

    @Override
    public String toString() {
        return "CacheKeyBo{" +
                "namespace='" + namespace + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
