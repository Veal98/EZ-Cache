package cn.itmtx.ezcache.lock;

import cn.itmtx.ezcache.lock.bo.RedisDistributeLockBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于 Redis 实现分布式锁
 */
public abstract class AbstractRedisDistributedLock implements IDistributedLock {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRedisDistributedLock.class);

    /**
     * key: lock key, value: RedisDistributeLockBo
     */
    private static final ThreadLocal<Map<String, RedisDistributeLockBo>> LOCK_MAP = new ThreadLocal<Map<String, RedisDistributeLockBo>>() {
        @Override
        protected Map<String, RedisDistributeLockBo> initialValue() {
            return new HashMap<>(4);
        }
    };

    protected static final String OK = "OK";

    /**
     * 加锁
     *
     * @param key    key
     * @param val    vale
     * @param expireMillis 过期时间
     * @return 是否设置成功
     */
    protected abstract boolean setnx(String key, String val, long expireMillis);

    /**
     * 解锁
     *
     * @param key key
     */
    protected abstract void del(String key);

    @Override
    public boolean tryLock(String key, long expireMillis) {
        boolean locked = setnx(key, OK, expireMillis);
        if (locked) {
            Map<String, RedisDistributeLockBo> lockBoMap = LOCK_MAP.get();
            RedisDistributeLockBo info = new RedisDistributeLockBo();
            info.setLeaseTimeMillis(expireMillis);
            info.setStartTimeMillis(System.currentTimeMillis());
            lockBoMap.put(key, info);
        }
        return locked;
    }

    @Override
    public void unlock(String key) {
        Map<String, RedisDistributeLockBo> lockBoMap = LOCK_MAP.get();
        RedisDistributeLockBo lockBo = null;
        if (null != lockBoMap) {
            lockBo = lockBoMap.remove(key);
        }
        // 锁实际使用时间
        long useTime = System.currentTimeMillis() - lockBo.getStartTimeMillis();
        // 如果锁的实际使用时间超过了租约时间，则认为锁已经过期（或可能已经被其他进程释放），不执行后续的解锁操作
        if (useTime >= lockBo.getLeaseTimeMillis()) {
            logger.warn("lock(" + key + ") run timeout, use time:" + useTime);
            return;
        }
        try {
            del(key);
        } catch (Throwable e) {
            logger.error("redis distributeLock del failed.", e);
        }
    }
}
