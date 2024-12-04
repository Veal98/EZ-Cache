package cn.itmtx.ezcache.parser;

public class ExpressionParser {

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
}
