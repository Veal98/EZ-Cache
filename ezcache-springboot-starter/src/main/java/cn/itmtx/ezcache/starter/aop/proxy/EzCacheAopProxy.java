package cn.itmtx.ezcache.starter.aop.proxy;

import cn.itmtx.ezcache.core.proxy.ICacheProxy;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class EzCacheAopProxy implements ICacheProxy {

    private final MethodInvocation invocation;

    private Method method;

    public EzCacheAopProxy(MethodInvocation invocation) {
        this.invocation = invocation;
    }

    @Override
    public Object[] getArgs() {
        return invocation.getArguments();
    }

    @Override
    public Object getTarget() {
        return invocation.getThis();
    }

    @Override
    public Method getMethod() {
        if (null == method) {
            this.method = invocation.getMethod();
        }
        return method;
    }

    @Override
    public Object doProxy(Object[] arguments) throws Throwable {
        return getMethod().invoke(invocation.getThis(), arguments);
    }
}
