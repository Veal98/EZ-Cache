package cn.itmtx.ezcache.cacher;

import cn.itmtx.ezcache.bo.CacheBatchSetBo;
import cn.itmtx.ezcache.bo.CacheKeyBo;
import cn.itmtx.ezcache.bo.CacheWrapper;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 缓存操作封装
 */
public interface ICacheOperator {

    /**
     * 写数据到 cache
     * @param cacheKeyBo key
     * @param cacheWrapper 缓存数据
     */
    void setCache(CacheKeyBo cacheKeyBo, CacheWrapper cacheWrapper);

    /**
     * 批量写数据
     * @param cacheBatchSetBos
     */
    void batchSetCache(final Set<CacheBatchSetBo> cacheBatchSetBos);

    /**
     * 读数据
     * @param cacheKeyBo
     * @return
     */
    CacheWrapper<Object> getCache(CacheKeyBo cacheKeyBo);

    /**
     * 批量读数据
     *
     * @param keys   缓存keys
     * @return 返回已命中的缓存数据(要过滤未命中数据)
     * @throws Exception
     */
    Map<CacheKeyBo, CacheWrapper<Object>> batchGetCache(final Set<CacheKeyBo> keys) throws Exception;

    /**
     * 删除缓存
     * @param cacheKeyBos
     */
    void deleteCache(final Set<CacheKeyBo> cacheKeyBos);
}
