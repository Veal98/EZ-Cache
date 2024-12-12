package cn.itmtx.ezcache.redis;

import cn.itmtx.ezcache.bo.CacheBatchByteSetBo;
import cn.itmtx.ezcache.cacher.redis.AbstractRedisCacheOperator;
import cn.itmtx.ezcache.cacher.redis.IRedisClient;
import cn.itmtx.ezcache.serializer.ISerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @Author jc.yin
 * @Date 2024/12/11
 * @Description Spring Redis 管理
 **/
public class SpringRedisCacheOperator extends AbstractRedisCacheOperator {

    private static final Logger log = LoggerFactory.getLogger(SpringRedisCacheOperator.class);

    private final RedisConnectionFactory redisConnectionFactory;

    public SpringRedisCacheOperator(ISerializer<Object> serializer, RedisConnectionFactory redisConnectionFactory) {
        super(serializer);
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    protected IRedisClient getRedisClient() {
        return new SpringDataRedisClient(redisConnectionFactory, this);
    }

    public static class SpringDataRedisClient implements IRedisClient {
        private final RedisConnectionFactory redisConnectionFactory;
        private final RedisConnection redisConnection;

        private final AbstractRedisCacheOperator cacheOperator;

        public SpringDataRedisClient(RedisConnectionFactory redisConnectionFactory, AbstractRedisCacheOperator cacheOperator) {
            this.redisConnectionFactory = redisConnectionFactory;
            this.redisConnection = RedisConnectionUtils.getConnection(redisConnectionFactory);
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
