package cn.itmtx.ezcache.parser;

import cn.itmtx.ezcache.common.annotation.EzCache;

public interface IExpressionParser {

    /**
     * 是否开启自动刷新
     * @param ezCache
     * @return
     */
    boolean isAutoRefreshable(EzCache ezCache, Object target, Object[] args, Object retVal) throws Exception;

    /**
     * 是否允许缓存
     *
     * @param ezCache     Cache
     * @param target    AOP 拦截到的实例
     * @param args 参数
     * @param retVal    缓存数据
     * @return cacheAble 是否可以进行缓存
     * @throws Exception 异常
     */
    boolean isCacheable(EzCache ezCache, Object target, Object[] args, Object retVal) throws Exception;

    /**
     * 解析表达式，获取缓存 key
     * @param exp       表达式
     * @param target    AOP 拦截到的实例
     * @param args 参数
     * @param retVal    拦截到的方法结果值
     * @param hasRetVal 是否使用 retVal 参数
     * @throws Exception 异常
     */
    String parseCacheKeyFromExpression(String exp, Object target, Object[] args, Object retVal, boolean hasRetVal) throws Exception;

}
