package cn.itmtx.ezcache.cacher.redis;

import cn.itmtx.ezcache.bo.CacheKeyBo;
import cn.itmtx.ezcache.bo.CacheWrapper;
import cn.itmtx.ezcache.cacher.ICacheOperator;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @Author jc.yin
 * @Date 2024/12/10
 * @Description
 **/
public abstract class AbstractRedisCacheOperator implements ICacheOperator {
    /**
     * 写数据到 cache
     *
     * @param cacheKeyBo   key
     * @param cacheWrapper 缓存数据
     * @param method       方法
     */
    @Override
    public void set(CacheKeyBo cacheKeyBo, CacheWrapper cacheWrapper, Method method) {

    }

    /**
     * 根据 key 从 cache 中读取数据
     *
     * @param cacheKeyBo
     * @param method
     * @return
     */
    @Override
    public CacheWrapper<Object> get(CacheKeyBo cacheKeyBo, Method method) {
        return null;
    }

    /**
     * 删除缓存
     *
     * @param cacheKeyBos
     */
    @Override
    public void delete(Set<CacheKeyBo> cacheKeyBos) {

    }
}
