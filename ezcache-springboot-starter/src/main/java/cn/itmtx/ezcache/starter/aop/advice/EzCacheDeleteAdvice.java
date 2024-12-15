package cn.itmtx.ezcache.starter.aop.advice;

import cn.itmtx.ezcache.common.annotation.EzCacheDelete;
import cn.itmtx.ezcache.core.CacheProcessor;
import cn.itmtx.ezcache.starter.autoconfigure.EzCacheProperties;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Advice
 * 对注解 {@link EzCacheDelete} 开启 AOP 方法拦截
 */
public class EzCacheDeleteAdvice implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(EzCacheDeleteAdvice.class);

    private final CacheProcessor cacheProcessor;

    private final EzCacheProperties ezCacheProperties;

    public EzCacheDeleteAdvice(CacheProcessor cacheProcessor, EzCacheProperties ezCacheProperties) {
        this.cacheProcessor = cacheProcessor;
        this.ezCacheProperties = ezCacheProperties;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return null;
    }
}
