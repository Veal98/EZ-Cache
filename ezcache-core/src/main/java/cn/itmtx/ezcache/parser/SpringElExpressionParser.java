package cn.itmtx.ezcache.parser;

/**
 * @Author jc.yin
 * @Date 2024/12/10
 * @Description
 **/
public class SpringElExpressionParser extends AbstractExpressionParser{
    /**
     * 将表达式转换期望的值
     *
     * @param exp       生成缓存Key的表达式
     * @param target    AOP 拦截到的实例
     * @param args      参数
     * @param retVal    结果值（缓存数据）
     * @param hasRetVal 是否使用 retVal 参数
     * @param valueType 表达式最终返回值类型
     * @return T value 返回值
     * @throws Exception 异常
     */
    @Override
    public <T> T getElValue(String exp, Object target, Object[] args, Object retVal, boolean hasRetVal, Class<T> valueType) throws Exception {
        return null;
    }
}
