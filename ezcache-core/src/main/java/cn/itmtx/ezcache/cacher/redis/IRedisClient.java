package cn.itmtx.ezcache.cacher.redis;

import cn.itmtx.ezcache.bo.CacheKeyBo;
import cn.itmtx.ezcache.bo.CacheWrapper;

import java.io.Closeable;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * Redis缓存操作
 *
 *
 */
public interface IRedisClient extends Closeable {

    void set(final byte[] key, final byte[] value);

    void setex(final byte[] key, final int seconds, final byte[] value);

    byte[] get(byte[] key);

    /**
     * 根据缓存Key获得缓存中的数据
     *
     * @param returnType 返回值类型
     * @param keys       缓存keys
     * @return 缓存数据
     */
    Map<CacheKeyBo, CacheWrapper<Object>> mget(final Type returnType, final Set<CacheKeyBo> keys) throws Exception;

    /**
     * 批量删除
     *
     * @param keys   缓存keys
     */
    void delete(Set<CacheKeyBo> keys);
}
