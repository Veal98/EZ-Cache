package cn.itmtx.ezcache.lock.jedis;

import cn.itmtx.ezcache.lock.AbstractRedisDistributedLock;

/**
 * TODO Jedis(集群模式) 实现分布式锁
 */
public class JedisClusterDistributedLock extends AbstractRedisDistributedLock {

    @Override
    protected boolean setnx(String key, String val, long expireMillis) {
        return false;
    }

    @Override
    protected void del(String key) {

    }
}
