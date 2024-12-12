package cn.itmtx.ezcache.bo;

import java.util.Objects;

/**
 * @Description 批量 set cache
 **/
public class CacheBatchSetBo {

    private CacheKeyBo cacheKeyBo;

    private CacheWrapper<Object> result;

    public CacheKeyBo getCacheKeyBo() {
        return cacheKeyBo;
    }

    public void setCacheKeyBo(CacheKeyBo cacheKeyBo) {
        this.cacheKeyBo = cacheKeyBo;
    }

    public CacheWrapper<Object> getResult() {
        return result;
    }

    public void setResult(CacheWrapper<Object> result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CacheBatchSetBo that = (CacheBatchSetBo) o;
        return Objects.equals(cacheKeyBo, that.cacheKeyBo) && Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cacheKeyBo, result);
    }

    @Override
    public String toString() {
        return "CacheBatchSetBo{" +
                "cacheKeyBo=" + cacheKeyBo +
                ", result=" + result +
                '}';
    }
}
