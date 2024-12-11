package cn.itmtx.ezcache.redis;

import cn.itmtx.ezcache.bo.CacheKeyBo;
import cn.itmtx.ezcache.bo.CacheWrapper;
import cn.itmtx.ezcache.cacher.redis.AbstractRedisCacheOperator;
import cn.itmtx.ezcache.cacher.redis.IRedisClient;
import cn.itmtx.ezcache.serializer.ISerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;

import java.lang.reflect.Type;
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
    protected IRedisClient getRedis() {
        return new RedisConnectionClient(redisConnectionFactory, this);
    }

    public static class RedisConnectionClient implements IRedisClient {
        private final RedisConnectionFactory redisConnectionFactory;
        private final RedisConnection redisConnection;

        private final AbstractRedisCacheOperator cacheOperator;

        public RedisConnectionClient(RedisConnectionFactory redisConnectionFactory, AbstractRedisCacheOperator cacheOperator) {
            this.redisConnectionFactory = redisConnectionFactory;
            this.redisConnection = RedisConnectionUtils.getConnection(redisConnectionFactory);
            this.cacheOperator = cacheOperator;
        }

        @Override
        public void close() {
            RedisConnectionUtils.releaseConnection(redisConnection, redisConnectionFactory);
        }

        @Override
        public void set(byte[] key, byte[] value) {
            redisConnection.stringCommands().set(key, value);
        }

        @Override
        public void setex(byte[] key, int seconds, byte[] value) {
            redisConnection.stringCommands().setEx(key, seconds, value);
        }

        @Override
        public byte[] get(byte[] key) {
            return redisConnection.stringCommands().get(key);
        }


        @Override
        public Map<CacheKeyBo, CacheWrapper<Object>> mget(Type returnType, Set<CacheKeyBo> keys) throws Exception {
            String cacheKey;
            byte[] key;
            redisConnection.openPipeline();
            for (CacheKeyBo cacheKeyTO : keys) {
                cacheKey = cacheKeyTO.getCacheKey();
                if (null == cacheKey || cacheKey.isEmpty()) {
                    continue;
                }
                key = AbstractRedisCacheOperator.KEY_SERIALIZER.serialize(cacheKey);
                redisConnection.stringCommands().get(key);
            }

            return cacheOperator.deserialize(keys, redisConnection.closePipeline(), returnType);
        }

        @Override
        public void delete(Set<CacheKeyBo> keys) {
            try {
                redisConnection.openPipeline();
                for (CacheKeyBo cacheKeyBo : keys) {
                    String cacheKey = cacheKeyBo.getCacheKey();
                    if (null == cacheKey || cacheKey.length() == 0) {
                        continue;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("delete cache {}", cacheKey);
                    }
                    redisConnection.keyCommands().del(KEY_SERIALIZER.serialize(cacheKey));
                }
            } finally {
                redisConnection.closePipeline();
            }

        }


    }
}
