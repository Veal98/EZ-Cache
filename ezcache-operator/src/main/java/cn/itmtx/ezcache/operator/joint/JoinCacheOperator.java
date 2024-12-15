package cn.itmtx.ezcache.operator.joint;

import cn.itmtx.ezcache.operator.ICacheOperator;

/**
 * 联合缓存
 */
public class JoinCacheOperator {

    /**
     * 本地缓存
     */
    private ICacheOperator localCacheOperator;

    /**
     * 分布式缓存
     */
    private ICacheOperator remoteCacheOperator;

}
