package cn.itmtx.ezcache.lock.jedis;

import cn.itmtx.ezcache.lock.AbstractRedisDistributedLock;

/**
 * TODO Jedis(分片模式) 实现分布式锁
 */
public class ShardedJedisDistributedLock extends AbstractRedisDistributedLock {

    @Override
    protected boolean setnx(String key, String val, long expireMillis) {
        return false;
    }

    @Override
    protected void del(String key) {

    }
}
