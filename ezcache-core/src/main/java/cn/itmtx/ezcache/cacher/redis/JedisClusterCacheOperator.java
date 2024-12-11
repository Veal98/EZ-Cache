package cn.itmtx.ezcache.cacher.redis;

import cn.itmtx.ezcache.bo.CacheKeyBo;
import cn.itmtx.ezcache.bo.CacheWrapper;
import cn.itmtx.ezcache.serializer.ISerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.lang.reflect.Type;
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
    protected IRedisClient getRedis() {
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
        public void close() throws IOException {

        }

        @Override
        public void set(byte[] key, byte[] value) {
            jedisCluster.set(key, value);
        }

        @Override
        public void setex(byte[] key, int seconds, byte[] value) {
            jedisCluster.setex(key, seconds, value);
        }

        @Override
        public byte[] get(byte[] key) {
            return jedisCluster.get(key);
        }

        @Override
        public Map<CacheKeyBo, CacheWrapper<Object>> mget(Type returnType, Set<CacheKeyBo> keys) throws Exception {
            AbstractRetryableJedisClusterPipeline abstractRetryableJedisClusterPipeline = new AbstractRetryableJedisClusterPipeline(jedisCluster) {
                @Override
                public void execute(JedisClusterPipeline pipeline) {
                    JedisUtil.executeMGet(pipeline, keys);
                }
            };
            return cacheOperator.deserialize(keys, abstractRetryableJedisClusterPipeline.syncAndReturnAll(), returnType);
        }

        @Override
        public void delete(Set<CacheKeyBo> keys) {
            AbstractRetryableJedisClusterPipeline abstractRetryableJedisClusterPipeline = new AbstractRetryableJedisClusterPipeline(jedisCluster) {
                @Override
                public void execute(JedisClusterPipeline pipeline) {
                    JedisUtil.executeDelete(pipeline, keys);
                }
            };
            try {
                abstractRetryableJedisClusterPipeline.sync();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

    }
}
