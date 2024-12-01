package cn.itmtx.ezcache.enums;

/**
 * operation type for cache
 */
public enum EzCacheOpTypeEnum {

    /**
     * only read data from cache
     * 只从缓存中读取
     */
    CACHE_READ_ONLY,

    /**
     * only read data from datasource, do nothing for cache
     * 只从数据源读取 (不读取缓存中的数据，也不写入缓存)
     */
    DATASOURCE_READ_ONLY,

    /**
     * read data from cache first. If there is no data in cache, load data from datasource and write it to cache.
     * 优先读缓存。如果缓存中没有数据，则从数据源加载数据，并更新到缓存中
     */
    CACHE_READ_DATASOURCE_LOAD,

    /**
     * load the latest data from datasource, and write it to cache
     * 从数据源读取最新的数据，并更新到缓存中
     */
    DATASOURCE_LOAD;
}
