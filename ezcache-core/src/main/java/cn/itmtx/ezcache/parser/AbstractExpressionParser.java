package cn.itmtx.ezcache.parser;

import cn.itmtx.ezcache.annotation.EzCache;

/**
 * 表达式解析
 */
public abstract class AbstractExpressionParser {

    /**
     * 是否允许缓存
     * @param ezCache
     * @param target
     * @param args
     * @return
     * @throws Exception
     */
    public boolean isCacheable(EzCache ezCache, Object target, Object[] args) throws Exception {
        boolean res = true;
        if (null != args && args.length > 0 && null != ezCache.condition() && ezCache.condition().length() > 0) {
            res = getElValue(ezCache.condition(), target, args, Boolean.class);
        }
        return res;
    }

    /**
     * 是否允许缓存
     *
     * @param ezCache     Cache
     * @param target    AOP 拦截到的实例
     * @param arguments 参数
     * @param retVal    缓存数据
     * @return cacheAble 是否可以进行缓存
     * @throws Exception 异常
     */
    public boolean isCacheable(EzCache ezCache, Object target, Object[] arguments, Object retVal) throws Exception {
        boolean rv = true;
        if (null != ezCache.condition() && ezCache.condition().length() > 0) {
            rv = this.getElValue(ezCache.condition(), target, arguments, retVal, true, Boolean.class);
        }
        return rv;
    }

    /**
     * 根据请求参数和执行结果值，构造缓存Key
     *
     * @param keyEL     生成缓存Key的表达式
     * @param target    AOP 拦截到的实例
     * @param arguments 参数
     * @param result    结果值
     * @param hasResult 是否有 result
     * @return CacheKey 缓存Key
     * @throws Exception 异常
     */
    public String getCacheKeyFromParseExpression(String keyEL, Object target, Object[] arguments, Object result, boolean hasResult)
            throws Exception {
        return null;
    }

    /**
     * 将表达式转换期望的值
     *
     * @param exp       生成缓存Key的表达式
     * @param target    AOP 拦截到的实例
     * @param args 参数
     * @param retVal    结果值（缓存数据）
     * @param hasRetVal 是否使用 retVal 参数
     * @param valueType 表达式最终返回值类型
     * @param <T>       泛型
     * @return T value 返回值
     * @throws Exception 异常
     */
    public abstract <T> T getElValue(String exp, Object target, Object[] args, Object retVal, boolean hasRetVal,
                                     Class<T> valueType) throws Exception;

    public <T> T getElValue(String keyEL, Object target, Object[] arguments, Class<T> valueType) throws Exception {
        return this.getElValue(keyEL, target, arguments, null, false, valueType);
    }
}
