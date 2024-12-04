package cn.itmtx.ezcache;

import cn.itmtx.ezcache.bo.CacheKeyBo;
import cn.itmtx.ezcache.bo.CacheWrapper;

import java.lang.reflect.Method;
import java.util.Set;

public interface ICacheOperator {


    /**
     * 写数据到 cache
     * @param cacheKeyBo key
     * @param cacheWrapper 缓存数据
     * @param method 方法
     */
    void set(CacheKeyBo cacheKeyBo, CacheWrapper cacheWrapper, Method method);

    /**
     * 根据 key 从 cache 中读取数据
     * @param cacheKeyBo
     * @param method
     * @return
     */
    CacheWrapper<Object> get(CacheKeyBo cacheKeyBo, Method method);

    /**
     * 删除缓存
     * @param cacheKeyBos
     */
    void delete(final Set<CacheKeyBo> cacheKeyBos);
}
