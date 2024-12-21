package cn.itmtx.ezcache.operator.redis;

import cn.itmtx.ezcache.serializer.ISerializer;

/**
 * TODO Jedis(集群模式) 管理 Redis
 */
public class JedisClusterCacheOperator extends AbstractRedisCacheOperator{


    public JedisClusterCacheOperator(ISerializer<Object> serializer) {
        super(serializer);
    }

    @Override
    protected IRedisClient getRedisClient() {
        return null;
    }
}
