package cn.itmtx.ezcache.common.bo;

/**
 * 缓存处理相关的配置
 */
public class EzCacheConfigBo {

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * 自动刷新：处理自动刷新队列的线程数量
     */
    private Integer threadCnt = 10;

    /**
     * 自动刷新：自动刷新队列中允许存放的最大容量
     */
    private int maxElementSize = 20000;

    /**
     * 从 datasource 获取数据的 Processing Map 初始大小
     */
    private int processingMapSize = 512;

    /**
     * 主动刷新：主动刷新(异步)队列的容量
     */
    private int asyncRefreshQueueCapacity = 2000;

    /**
     * 主动刷新：主动刷新(异步)线程池的 corePoolSize
     */
    private int asyncRefreshThreadPoolSize = 2;

    /**
     * 主动刷新：主动刷新(异步)线程池的 maximumPoolSize
     */
    private int asyncRefreshThreadPoolMaxSize = 20;

    /**
     * 主动刷新：主动刷新(异步)线程池的 keepAliveTime（单位，毫秒）
     */
    private int asyncRefreshThreadPoolKeepAliveTimeMillis = 20000;

    public int getProcessingMapSize() {
        return processingMapSize;
    }

    public void setProcessingMapSize(int processingMapSize) {
        this.processingMapSize = processingMapSize;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Integer getThreadCnt() {
        return threadCnt;
    }

    public void setThreadCnt(Integer threadCnt) {
        this.threadCnt = threadCnt;
    }

    public int getMaxElementSize() {
        return maxElementSize;
    }

    public void setMaxElementSize(int maxElementSize) {
        this.maxElementSize = maxElementSize;
    }

    public int getAsyncRefreshQueueCapacity() {
        return asyncRefreshQueueCapacity;
    }

    public void setAsyncRefreshQueueCapacity(int asyncRefreshQueueCapacity) {
        this.asyncRefreshQueueCapacity = asyncRefreshQueueCapacity;
    }

    public int getAsyncRefreshThreadPoolSize() {
        return asyncRefreshThreadPoolSize;
    }

    public void setAsyncRefreshThreadPoolSize(int asyncRefreshThreadPoolSize) {
        this.asyncRefreshThreadPoolSize = asyncRefreshThreadPoolSize;
    }

    public int getAsyncRefreshThreadPoolMaxSize() {
        return asyncRefreshThreadPoolMaxSize;
    }

    public void setAsyncRefreshThreadPoolMaxSize(int asyncRefreshThreadPoolMaxSize) {
        this.asyncRefreshThreadPoolMaxSize = asyncRefreshThreadPoolMaxSize;
    }

    public int getAsyncRefreshThreadPoolKeepAliveTimeMillis() {
        return asyncRefreshThreadPoolKeepAliveTimeMillis;
    }

    public void setAsyncRefreshThreadPoolKeepAliveTimeMillis(int asyncRefreshThreadPoolKeepAliveTimeMillis) {
        this.asyncRefreshThreadPoolKeepAliveTimeMillis = asyncRefreshThreadPoolKeepAliveTimeMillis;
    }
}
