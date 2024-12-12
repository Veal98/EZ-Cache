package cn.itmtx.ezcache.cacher.redis;

import cn.itmtx.ezcache.bo.CacheBatchByteSetBo;

import java.io.Closeable;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * 直接操作 Redis 的 client
 */
public interface IRedisClient extends Closeable {

    void set(final byte[] key, final byte[] value);

    void setex(final byte[] key, final long millSeconds, final byte[] value);

    byte[] get(byte[] key);

    /**
     * 批量 set
     * @param cacheBatchByteSetBos
     */
    void mset(final Set<CacheBatchByteSetBo> cacheBatchByteSetBos);

    /**
     * 批量 get
     *
     * @param keyBytes    cache key(序列化后的)
     * @return key: 序列化的 cache key, value: 序列化的 cache value
     */
    Map<byte[], byte[]> mget(final Set<byte[]> keyBytes) throws Exception;

    /**
     * 批量删除
     *
     * @param keyBytes cache key(序列化后的)
     */
    void delete(Set<byte[]> keyBytes);
}
