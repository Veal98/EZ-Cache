package cn.itmtx.ezcache;

import cn.itmtx.ezcache.annotation.EzCache;
import cn.itmtx.ezcache.bo.AutoLoadBo;
import cn.itmtx.ezcache.bo.EzCacheBo;
import cn.itmtx.ezcache.bo.EzCacheKeyBo;
import cn.itmtx.ezcache.proxy.IEzCacheProxy;

public class DataLoader {

    private EzCacheProcessor cacheProcessor;

    private IEzCacheProxy proxy;

    private EzCacheKeyBo cacheKeyBo;

    private EzCache ezCache;

    private Object[] args;

    private AutoLoadBo autoLoadBo;

    /**
     * 是否是第一个 datasource 请求
     */
    private boolean isFirst;

    private long loadDataUseTimeMills;

    private EzCacheBo<Object> cacheBo;

    private int tryCount = 0;

    public DataLoader() {
    }

    public DataLoader init(IEzCacheProxy proxy, Object[] args, AutoLoadBo autoLoadBo, EzCache ezCache, EzCacheProcessor cacheProcessor) {
        this.cacheProcessor = cacheProcessor;
        this.proxy = proxy;
        this.args = args;
        this.autoLoadBo = autoLoadBo;
        this.ezCache = ezCache;

        if (null == autoLoadBo) {
            // 用户请求
            this.args = proxy.getArgs();
        } else {
            // 来自 AutoLoadProcessor 的请求
            this.args = autoLoadBo.getArgs();
        }
        this.loadDataUseTimeMills = 0;
        this.tryCount = 0;
        return this;
    }

    /**
     * 重置数据
     */
    public void reset() {
    }

    public DataLoader loadData() throws Throwable {
        return null;
    }

    public DataLoader getData() throws Throwable {
        return null;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public EzCacheBo<Object> getCacheBo() {
        return null;
    }

    public long getLoadDataUseTimeMills() {
        return loadDataUseTimeMills;
    }

}
