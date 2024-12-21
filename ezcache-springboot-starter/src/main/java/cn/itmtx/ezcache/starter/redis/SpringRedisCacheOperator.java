package cn.itmtx.ezcache.starter.redis;

import cn.itmtx.ezcache.operator.bo.CacheBatchByteSetBo;
import cn.itmtx.ezcache.operator.redis.AbstractRedisCacheOperator;
import cn.itmtx.ezcache.operator.redis.IRedisClient;
import cn.itmtx.ezcache.serializer.ISerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author jc.yin
 * @Date 2024/12/11
 * @Description Spring Redis 管理
 **/
public class SpringRedisCacheOperator extends AbstractRedisCacheOperator {

    private static final Logger log = LoggerFactory.getLogger(SpringRedisCacheOperator.class);

    /**
     * Redis 连接池工厂
     */
    private final RedisConnectionFactory redisConnectionFactory;

    /**
     * 默认用 Jedis 连接池（TODO，新增 Lettuce 支持）
     * @param serializer
     */
    public SpringRedisCacheOperator(ISerializer<Object> serializer) {
        super(serializer);

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 设置连接池参数，例如最大连接数、最大空闲连接数等
        poolConfig.setMaxTotal(100);
        poolConfig.setMaxIdle(30);
        poolConfig.setMinIdle(10);
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(poolConfig);
        jedisConnectionFactory.afterPropertiesSet();

        this.redisConnectionFactory = jedisConnectionFactory;
    }

    public SpringRedisCacheOperator(RedisConnectionFactory redisConnectionFactory, ISerializer<Object> serializer) {
        super(serializer);
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    protected IRedisClient getRedisClient() {
        return new SpringDataRedisClient(redisConnectionFactory);
    }

    public static class SpringDataRedisClient implements IRedisClient {
        private final RedisConnectionFactory redisConnectionFactory;
        private final RedisConnection redisConnection;

        public SpringDataRedisClient(RedisConnectionFactory redisConnectionFactory) {
            this.redisConnectionFactory = redisConnectionFactory;
            this.redisConnection = RedisConnectionUtils.getConnection(redisConnectionFactory);
        }

        @Override
        public void set(byte[] key, byte[] value) {
            redisConnection.stringCommands().set(key, value);
        }

        @Override
        public void setex(byte[] key, long millSeconds, byte[] value) {
            redisConnection.stringCommands().setEx(key, millSeconds / 10 , value);
        }

        /**
         * 批量 set
         *
         * @param cacheBatchByteSetBos
         */
        @Override
        public void mset(Set<CacheBatchByteSetBo> cacheBatchByteSetBos) {
            try {
                redisConnection.openPipeline();
                for (CacheBatchByteSetBo byteSetBo: cacheBatchByteSetBos) {
                    byte[] keyByte = byteSetBo.getKeyByte();
                    byte[] resultByte = byteSetBo.getResultByte();
                    long expireMillis = byteSetBo.getExpireMillis();
                    this.setex(keyByte, expireMillis, resultByte);
                }
            } finally {
                redisConnection.closePipeline();
            }
        }

        @Override
        public byte[] get(byte[] key) {
            return redisConnection.stringCommands().get(key);
        }

        /**
         * 批量 get
         *
         * @param keyBytes   cache key(序列化后的)
         * @return key: 序列化的 cache key, value: 序列化的 cache value
         */
        @Override
        public Map<byte[], byte[]> mget(Set<byte[]> keyBytes) {
            Map<byte[], byte[]> map = new HashMap<>();
            try {
                redisConnection.openPipeline();
                for(byte[] keyByte : keyBytes) {
                    byte[] valueBytes = this.get(keyByte);
                    if (valueBytes != null && valueBytes.length > 0) {
                        map.put(keyByte, valueBytes);
                    }
                }
            } finally {
                redisConnection.closePipeline();
            }

            return map;
        }

        /**
         * 批量删除
         *
         * @param keyBytes cache key(序列化后的)
         */
        @Override
        public void delete(Set<byte[]> keyBytes) {
            try {
                redisConnection.openPipeline();
                for (byte[] keyByte : keyBytes) {
                    redisConnection.keyCommands().del(keyByte);
                }
            } finally {
                redisConnection.closePipeline();
            }
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
            RedisConnectionUtils.releaseConnection(redisConnection, redisConnectionFactory);
        }
    }
}
