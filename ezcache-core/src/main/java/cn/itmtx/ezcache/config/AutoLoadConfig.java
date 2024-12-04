package cn.itmtx.ezcache.config;

public class AutoLoadConfig {

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * 处理自动加载队列的线程数量
     */
    private Integer threadCnt = 10;

    /**
     * 自动加载队列中允许存放的最大容量
     */
    private int maxElementSize = 20000;

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
}
