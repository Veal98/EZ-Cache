package cn.itmtx.ezcache.starter.redis;

import cn.itmtx.ezcache.lock.AbstractRedisDistributedLock;
import cn.itmtx.ezcache.serializer.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.types.Expiration;

import java.util.concurrent.TimeUnit;

public class SpringRedisDistributedLock extends AbstractRedisDistributedLock {


    private static final Logger logger = LoggerFactory.getLogger(SpringRedisDistributedLock.class);

    private static final StringSerializer STRING_SERIALIZER = new StringSerializer();

    private final RedisConnectionFactory redisConnectionFactory;

    public SpringRedisDistributedLock(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    private RedisConnection getConnection() {
        return RedisConnectionUtils.getConnection(redisConnectionFactory);
    }

    @Override
    protected boolean setnx(String key, String val, long expireMillis) {
        if (null == redisConnectionFactory || null == key || key.isEmpty()) {
            return false;
        }
        RedisConnection redisConnection = getConnection();
        try {
            Expiration expiration = Expiration.from(expireMillis, TimeUnit.MILLISECONDS);
            Boolean locked = redisConnection.stringCommands().set(STRING_SERIALIZER.serialize(key), STRING_SERIALIZER.serialize(val), expiration, RedisStringCommands.SetOption.SET_IF_ABSENT);
            return locked != null && locked;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            RedisConnectionUtils.releaseConnection(redisConnection, redisConnectionFactory);
        }
        return false;
    }

    @Override
    protected void del(String key) {
        if (null == redisConnectionFactory || null == key || key.length() == 0) {
            return;
        }
        RedisConnection redisConnection = getConnection();
        try {
            redisConnection.keyCommands().del(STRING_SERIALIZER.serialize(key));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            RedisConnectionUtils.releaseConnection(redisConnection, redisConnectionFactory);
        }
    }
}
