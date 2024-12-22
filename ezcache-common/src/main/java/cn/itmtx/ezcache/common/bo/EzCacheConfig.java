package cn.itmtx.ezcache.common.bo;

/**
 * 缓存处理相关的配置
 */
public class EzCacheConfig {

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * 自动刷新：处理自动刷新队列的线程数量
     */
    private Integer autoRefreshThreadCount = 10;

    /**
     * 自动刷新：自动刷新队列的容量
     */
    private int autoRefreshMaxQueueCapacity = 20000;

    /**
     * 自动刷新队列的排序算法：AutoRefreshQueueSortTypeEnum.getCode
     */
    private int autoRefreshQueueSortType = 0;

    /**
     * 从 datasource 获取数据的 Processing Map 初始大小
     */
    private int processingMapSize = 512;

    /**
     * 默认同步主动刷新，若为 true 则开启异步主动刷新
     */
    private boolean asyncRefresh = false;

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

    /**
     * 允许 Map 的不持久化变更数 (若缓存选择 Map，当 Map 变更数量超过此值时才做持久化操作即存入磁盘)
     */
    private int mapUnPersistCountThreshold = 0;

    public int getAutoRefreshQueueSortType() {
        return autoRefreshQueueSortType;
    }

    public void setAutoRefreshQueueSortType(int autoRefreshQueueSortType) {
        this.autoRefreshQueueSortType = autoRefreshQueueSortType;
    }

    public boolean isAsyncRefresh() {
        return asyncRefresh;
    }

    public void setAsyncRefresh(boolean asyncRefresh) {
        this.asyncRefresh = asyncRefresh;
    }

    public int getMapUnPersistCountThreshold() {
        return mapUnPersistCountThreshold;
    }

    public void setMapUnPersistCountThreshold(int mapUnPersistCountThreshold) {
        this.mapUnPersistCountThreshold = mapUnPersistCountThreshold;
    }

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

    public Integer getAutoRefreshThreadCount() {
        return autoRefreshThreadCount;
    }

    public void setAutoRefreshThreadCount(Integer autoRefreshThreadCount) {
        this.autoRefreshThreadCount = autoRefreshThreadCount;
    }

    public int getAutoRefreshMaxQueueCapacity() {
        return autoRefreshMaxQueueCapacity;
    }

    public void setAutoRefreshMaxQueueCapacity(int autoRefreshMaxQueueCapacity) {
        this.autoRefreshMaxQueueCapacity = autoRefreshMaxQueueCapacity;
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
