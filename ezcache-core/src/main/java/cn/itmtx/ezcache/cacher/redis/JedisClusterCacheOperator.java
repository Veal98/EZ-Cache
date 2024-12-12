package cn.itmtx.ezcache.cacher.redis;

import cn.itmtx.ezcache.bo.CacheBatchByteSetBo;
import cn.itmtx.ezcache.serializer.ISerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @Author jc.yin
 * @Date 2024/12/10
 * @Description
 **/
public class JedisClusterCacheOperator extends AbstractRedisCacheOperator {

    private static final Logger log = LoggerFactory.getLogger(JedisClusterCacheOperator.class);

    private final JedisClusterClient jedisClusterClient;

    public JedisClusterCacheOperator(JedisCluster jedisCluster, ISerializer<Object> serializer) {
        super(serializer);
        this.jedisClusterClient = new JedisClusterClient(jedisCluster, this);
    }

    @Override
    protected IRedisClient getRedisClient() {
        return jedisClusterClient;
    }

    /**
     * Jedis 操作 Redis
     */
    public static class JedisClusterClient implements IRedisClient {

        private final JedisCluster jedisCluster;

        private final AbstractRedisCacheOperator cacheOperator;

        public JedisClusterClient(JedisCluster jedisCluster, AbstractRedisCacheOperator cacheOperator) {
            this.jedisCluster = jedisCluster;
            this.cacheOperator = cacheOperator;
        }


        @Override
        public void set(byte[] key, byte[] value) {

        }

        @Override
        public void setex(byte[] key, long millSeconds, byte[] value) {

        }

        @Override
        public byte[] get(byte[] key) {
            return new byte[0];
        }

        /**
         * 批量 set
         *
         * @param cacheBatchByteSetBos
         */
        @Override
        public void mset(Set<CacheBatchByteSetBo> cacheBatchByteSetBos) {

        }

        /**
         * 批量 get
         *
         * @param keyBytes   cache key(序列化后的)
         * @return key: 序列化的 cache key, value: 序列化的 cache value
         */
        @Override
        public Map<byte[], byte[]> mget(Set<byte[]> keyBytes) throws Exception {
            return Collections.emptyMap();
        }

        /**
         * 批量删除
         *
         * @param keyBytes cache key(序列化后的)
         */
        @Override
        public void delete(Set<byte[]> keyBytes) {

        }


        /**
         * Closes this stream and releases any system resources associated
         * with it. If the stream is already closed then invoking this
         * method has no effect.
         *
         * <p> As noted in {@link AutoCloseable#close()}, cases where the
         * close may fail require careful attention. It is strongly advised
         * to relinquish the underlying resources and to internally
         * <em>mark</em> the {@code Closeable} as closed, prior to throwing
         * the {@code IOException}.
         *
         * @throws IOException if an I/O error occurs
         */
        @Override
        public void close() throws IOException {

        }
    }
}
