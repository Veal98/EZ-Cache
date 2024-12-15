package cn.itmtx.ezcache.lock;

/**
 * 分布式锁
 */
public interface IDistributedLock {

    /**
     * 获取分布式锁
     *
     * @param key        锁Key
     * @param expireMillis 锁的缓存时间（单位：毫秒）
     * @return boolean
     */
    boolean tryLock(String key, long expireMillis);

    /**
     * 释放锁
     *
     * @param key 锁Key
     */
    void unlock(String key);

}
