package cn.itmtx.ezcache.parser;

import cn.itmtx.ezcache.common.constant.CommonConstant;
import cn.itmtx.ezcache.common.utils.EzCacheUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author jc.yin
 * @Date 2024/12/10
 * @Description
 **/
public class SpringElExpressionParser extends AbstractExpressionParser {

    private final ExpressionParser parser = new SpelExpressionParser();

    private final ConcurrentHashMap<String, Expression> expCache = new ConcurrentHashMap<String, Expression>();

    /**
     * 表达式内置 hash 函数, 可在表达式中使用 #hash(sth)
     */
    private static Method HASH_FUNC = null;

    /**
     * 表达式内置 empty 函数, 可在表达式中使用 #empty(sth)
     */
    private static Method EMPTY_FUNC = null;

    static {
        try {
            HASH_FUNC = EzCacheUtils.class.getDeclaredMethod("getUniqueHashStr", new Class[]{Object.class});
            EMPTY_FUNC = EzCacheUtils.class.getDeclaredMethod("isEmpty", new Class[]{Object.class});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    @Override
    protected  <T> T parseExpression(String exp, Object target, Object[] args, Object retVal, boolean hasRetVal, Class<T> expValueType) throws Exception {
        if (expValueType.equals(String.class)) {
            // 如果期望的返回类型是String，并且表达式中不包含#（SpEL的标识符）和单引号（可能的字符串字面量），则直接返回表达式字符串
            if (exp.indexOf(CommonConstant.POUND) == -1 && exp.indexOf("'") == -1) {
                return (T) exp;
            }
        }
        // 创建StandardEvaluationContext实例，用于SpEL表达式的解析和求值
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 注册两个内置函数HASH和EMPTY，这些函数 用于SpEL表达式中
        context.registerFunction(AbstractExpressionParser.HASH_FUNC_NAME, HASH_FUNC);
        context.registerFunction(AbstractExpressionParser.EMPTY_FUNC_NAME, EMPTY_FUNC);

        // 将target、arguments和 retVal 设置为上下文变量，以便在SpEL表达式中使用
        context.setVariable(AbstractExpressionParser.TARGET_VAR_NAME, target);
        context.setVariable(AbstractExpressionParser.ARGS_VAR_NAME, args);
        if (hasRetVal) {
            context.setVariable(AbstractExpressionParser.RET_VAL_VAR_NAME, retVal);
        }

        // 尝试从缓存expCache中获取已解析的表达式。如果未缓存，则解析表达式并缓存
        Expression expression = expCache.get(exp);
        if (null == expression) {
            expression = parser.parseExpression(exp);
            expCache.put(exp, expression);
        }

        // 在给定的上下文中求值，并返回指定类型的值
        return expression.getValue(context, expValueType);
    }
}
