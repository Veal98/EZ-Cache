package cn.itmtx.ezcache.lock.bo;

public class RedisDistributeLockBo {

    /**
     * 开始时间
     */
    private Long startTimeMillis;

    /**
     * 租约时长
     */
    private Long leaseTimeMillis;

    public Long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(Long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public Long getLeaseTimeMillis() {
        return leaseTimeMillis;
    }

    public void setLeaseTimeMillis(Long leaseTimeMillis) {
        this.leaseTimeMillis = leaseTimeMillis;
    }
}
