package cn.itmtx.ezcache;

import cn.itmtx.ezcache.annotation.EzCache;
import cn.itmtx.ezcache.bo.AutoRefreshBo;
import cn.itmtx.ezcache.bo.CacheWrapper;
import cn.itmtx.ezcache.bo.CacheKeyBo;
import cn.itmtx.ezcache.proxy.ICacheProxy;

public class DataLoader {

    private CacheProcessor cacheProcessor;

    private ICacheProxy proxy;

    private CacheKeyBo cacheKeyBo;

    private EzCache ezCache;

    private Object[] args;

    private AutoRefreshBo autoRefreshBo;

    /**
     * 是否是第一个 datasource 请求
     */
    private boolean isFirst;

    /**
     * 从 datasource 加载数据所花费的时间
     */
    private long loadDataTimeMills;

    private CacheWrapper<Object> cacheWrapper;

    private int tryCount = 0;

    public DataLoader() {
    }

    public DataLoader init(ICacheProxy proxy, AutoRefreshBo autoRefreshBo, CacheKeyBo cacheKeyBo, EzCache ezCache, CacheProcessor cacheProcessor) {
        this.cacheProcessor = cacheProcessor;
        this.proxy = proxy;
        this.cacheKeyBo = cacheKeyBo;
        this.ezCache = ezCache;
        this.autoRefreshBo = autoRefreshBo;

        if (null == autoRefreshBo) {
            // 用户请求
            this.args = proxy.getArgs();
        } else {
            // 来自 AutoLoadProcessor 的请求
            this.args = autoRefreshBo.getArgs();
        }
        this.loadDataTimeMills = 0;
        this.tryCount = 0;
        return this;
    }

    public DataLoader init(ICacheProxy proxy, Object[] args, AutoRefreshBo autoRefreshBo, EzCache ezCache, CacheProcessor cacheProcessor) {
        this.cacheProcessor = cacheProcessor;
        this.proxy = proxy;
        this.args = args;
        this.autoRefreshBo = autoRefreshBo;
        this.ezCache = ezCache;

        if (null == autoRefreshBo) {
            // 用户请求
            this.args = proxy.getArgs();
        } else {
            // 来自 AutoLoadProcessor 的请求
            this.args = autoRefreshBo.getArgs();
        }
        this.loadDataTimeMills = 0;
        this.tryCount = 0;
        return this;
    }

    public DataLoader init(ICacheProxy proxy, CacheKeyBo cacheKey, EzCache ezCache, CacheProcessor cacheProcessor) {
        return init(proxy, null, cacheKey, ezCache, cacheProcessor);
    }

    /**
     * 重置数据
     */
    public void reset() {
    }

    /**
     * 处理从 datasource 中获取数据的并发控制和请求等待逻辑
     * @return
     * @throws Throwable
     */
    public DataLoader loadData(){
        return null;
    }

    /**
     * 真正从 datasource 中获取数据的方法
     * @return
     * @throws Throwable
     */
    public DataLoader getData(){
        return null;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public CacheWrapper<Object> getCacheWrapper() {
        return null;
    }

    public long getLoadDataTimeMills() {
        return loadDataTimeMills;
    }

}
