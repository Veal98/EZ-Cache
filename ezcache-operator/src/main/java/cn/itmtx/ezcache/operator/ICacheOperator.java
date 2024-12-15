package cn.itmtx.ezcache.operator;


import cn.itmtx.ezcache.common.bo.CacheKeyBo;
import cn.itmtx.ezcache.common.bo.CacheWrapper;
import cn.itmtx.ezcache.operator.bo.CacheBatchSetBo;

import java.util.Map;
import java.util.Set;

/**
 * 缓存操作器（封装缓存操作）
 */
public interface ICacheOperator {

    /**
     * 写数据到 cache
     * @param cacheKeyBo key
     * @param cacheWrapper 缓存数据
     */
    void setCache(CacheKeyBo cacheKeyBo, CacheWrapper<Object> cacheWrapper);

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

    void shutdown();
}
