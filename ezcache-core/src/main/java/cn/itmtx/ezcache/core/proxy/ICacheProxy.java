package cn.itmtx.ezcache.core.proxy;

import java.lang.reflect.Method;

/**
 * AOP 代理
 */
public interface ICacheProxy {

    /**
     * 获取参数
     * @return
     */
    Object[] getArgs();

    /**
     * 获取目标实例
     * @return
     */
    Object getTarget();

    /**
     * 获取方法
     *
     * @return Method
     */
    Method getMethod();

    /**
     * 执行方法
     *
     * @param args 参数
     * @return 方法执行结果
     * @throws Throwable Throwable
     */
    Object doProxy(Object[] args) throws Throwable;
}
