package cn.itmtx.ezcache.operator.redis;

import cn.itmtx.ezcache.serializer.ISerializer;

/**
 * TODO Jedis(分片模式) 管理 Redis
 */
public class ShardedJedisCacheOperator extends AbstractRedisCacheOperator{

    public ShardedJedisCacheOperator(ISerializer<Object> serializer) {
        super(serializer);
    }

    @Override
    protected IRedisClient getRedisClient() {
        return null;
    }
}
