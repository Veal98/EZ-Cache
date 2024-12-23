package cn.itmtx.ezcache.starter.admin;

public class AutoRefreshVo {

    private String namespace;

    /**
     * key 表达式解析后的结果
     */
    private String key;

    private String method;

    /**
     * 第一次请求时间
     */
    private String firstRequestTimeMillis;

    /**
     * 上次请求时间
     */
    private String lastRequestTimeMillis;

    /**
     * 请求次数
     */
    private long requestCount;

    /**
     * 缓存过期时长
     */
    private long expireTimeMillis;

    /**
     * 缓存到期时间
     */
    private String expireTimeStr;

    /**
     * 若开启自动刷新，缓存数据持续 autoRefreshNoRequestTimeoutMillis(单位：毫秒) 没有被使用，就关闭对此缓存数据的自动刷新
     * 如果 autoRefreshNoRequestTimeoutMillis 为 0 时，自动刷新会一直开启
     */
    private long autoRefreshNoRequestTimeoutMillis;

    /**
     * 停止自动刷新的时间
     */
    private String autoRefreshNoRequestTimeoutStr;

    /**
     * 上次从 datasource 加载数据的时间
     */
    private String lastLoadTimeStr;

    /**
     * 从 datasource 加载数据的次数
     */
    private long loadCount;

    /**
     * 从 datasource 加载数据的平均耗时
     */
    private long avgLoadDataTimeMillis;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getFirstRequestTimeMillis() {
        return firstRequestTimeMillis;
    }

    public void setFirstRequestTimeMillis(String firstRequestTimeMillis) {
        this.firstRequestTimeMillis = firstRequestTimeMillis;
    }

    public String getLastRequestTimeMillis() {
        return lastRequestTimeMillis;
    }

    public void setLastRequestTimeMillis(String lastRequestTimeMillis) {
        this.lastRequestTimeMillis = lastRequestTimeMillis;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(long requestCount) {
        this.requestCount = requestCount;
    }

    public long getExpireTimeMillis() {
        return expireTimeMillis;
    }

    public void setExpireTimeMillis(long expireTimeMillis) {
        this.expireTimeMillis = expireTimeMillis;
    }

    public String getExpireTimeStr() {
        return expireTimeStr;
    }

    public void setExpireTimeStr(String expireTimeStr) {
        this.expireTimeStr = expireTimeStr;
    }

    public long getAutoRefreshNoRequestTimeoutMillis() {
        return autoRefreshNoRequestTimeoutMillis;
    }

    public void setAutoRefreshNoRequestTimeoutMillis(long autoRefreshNoRequestTimeoutMillis) {
        this.autoRefreshNoRequestTimeoutMillis = autoRefreshNoRequestTimeoutMillis;
    }

    public String getAutoRefreshNoRequestTimeoutStr() {
        return autoRefreshNoRequestTimeoutStr;
    }

    public void setAutoRefreshNoRequestTimeoutStr(String autoRefreshNoRequestTimeoutStr) {
        this.autoRefreshNoRequestTimeoutStr = autoRefreshNoRequestTimeoutStr;
    }

    public String getLastLoadTimeStr() {
        return lastLoadTimeStr;
    }

    public void setLastLoadTimeStr(String lastLoadTimeStr) {
        this.lastLoadTimeStr = lastLoadTimeStr;
    }

    public long getLoadCount() {
        return loadCount;
    }

    public void setLoadCount(long loadCount) {
        this.loadCount = loadCount;
    }

    public long getAvgLoadDataTimeMillis() {
        return avgLoadDataTimeMillis;
    }

    public void setAvgLoadDataTimeMillis(long avgLoadDataTimeMillis) {
        this.avgLoadDataTimeMillis = avgLoadDataTimeMillis;
    }
}
