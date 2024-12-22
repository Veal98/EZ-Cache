package cn.itmtx.ezcache.parser;

import cn.itmtx.ezcache.common.annotation.EzCache;

public abstract class AbstractExpressionParser implements IExpressionParser{

    /**
     * 表达式内置 hash 函数
     */
    static final String HASH_FUNC_NAME = "hash";

    /**
     * 表达式内置 empty 函数
     */
    static final String EMPTY_FUNC_NAME = "empty";

    /**
     * 表达式内置变量 target
     */
    static final String TARGET_VAR_NAME = "target";

    /**
     * 表达式内置变量 args
     */
    static final String ARGS_VAR_NAME = "args";

    /**
     * 表达式内置变量 retVal
     */
    static final String RET_VAL_VAR_NAME = "retVal";

    /**
     * 是否开启自动刷新
     * @param ezCache
     * @return
     */
    @Override
    public boolean isAutoRefreshable(EzCache ezCache, Object target, Object[] args, Object retVal) throws Exception {
        boolean rv = true;
        if (null != ezCache.autoRefreshCondition() && ezCache.autoRefreshCondition().length() > 0) {
            rv = this.parseExpression(ezCache.autoRefreshCondition(), target, args, retVal, true, Boolean.class);
        }
        return rv;
    }

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
    @Override
    public boolean isCacheable(EzCache ezCache, Object target, Object[] args, Object retVal) throws Exception {
        boolean rv = true;
        if (null != ezCache.condition() && ezCache.condition().length() > 0) {
            rv = this.parseExpression(ezCache.condition(), target, args, retVal, true, Boolean.class);
        }
        return rv;
    }

    /**
     * 解析表达式，获取缓存 key
     * @param exp       表达式
     * @param target    AOP 拦截到的实例
     * @param args 参数
     * @param retVal    拦截到的方法结果值
     * @param hasRetVal 是否使用 retVal 参数
     * @throws Exception 异常
     */
    @Override
    public String parseCacheKeyFromExpression(String exp, Object target, Object[] args, Object retVal, boolean hasRetVal) throws Exception {
        return this.parseExpression(exp, target, args, retVal, hasRetVal, String.class);
    }

    /**
     * 解析表达式
     * @param exp       表达式
     * @param target    AOP 拦截到的实例
     * @param args 参数
     * @param retVal    拦截到的方法结果值
     * @param hasRetVal 是否使用 retVal 参数
     * @param expValueType 表达式最终返回值类型
     * @param <T>       泛型
     * @throws Exception 异常
     */
    protected abstract <T> T parseExpression(String exp, Object target, Object[] args, Object retVal, boolean hasRetVal, Class<T> expValueType) throws Exception;

    /**
     * 解析表达式
     * @param exp       表达式
     * @param target    AOP 拦截到的实例
     * @param args 参数
     * @param expValueType 表达式最终返回值类型
     * @param <T>       泛型
     * @throws Exception 异常
     */
    private <T> T parseExpression(String exp, Object target, Object[] args, Class<T> expValueType) throws Exception {
        return this.parseExpression(exp, target, args, null, false, expValueType);
    }
}
