package cn.itmtx.ezcache.enums;

public enum LockStateEnum {

    /**
     * 已获取到锁
     */
    LOCKED,

    /**
     * 未获取到锁
     */
    UN_LOCKED,

    /**
     * 获取锁异常
     */
    EXCEPTION;
}
