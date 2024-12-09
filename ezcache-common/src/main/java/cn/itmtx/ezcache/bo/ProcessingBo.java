package cn.itmtx.ezcache.bo;

/**
 * 正在处理中的 datasource 请求
 */
public class ProcessingBo {

    /**
     * 请求开始时间
     */
    private volatile long startTime;

    /**
     * 请求获取到的值
     */
    private volatile CacheWrapper<Object> cacheWrapper;

    /**
     * 若请求失败，填入 error
     */
    private volatile Throwable error;

    /**
     * 并发请求中的第一个请求是否执行完毕
     */
    private volatile boolean firstFinished = false;

    public ProcessingBo() {
        // new ProcessingBo 的时候初始化 startTime
        startTime = System.currentTimeMillis();
    }

    public boolean isFirstFinished() {
        return firstFinished;
    }

    public void setFirstFinished(boolean firstFinished) {
        this.firstFinished = firstFinished;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public CacheWrapper<Object> getCacheWrapper() {
        return cacheWrapper;
    }

    public void setCacheWrapper(CacheWrapper<Object> cacheWrapper) {
        this.cacheWrapper = cacheWrapper;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
