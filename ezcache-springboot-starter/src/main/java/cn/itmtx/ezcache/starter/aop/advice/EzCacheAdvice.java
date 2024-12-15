package cn.itmtx.ezcache.starter.aop.advice;

import cn.itmtx.ezcache.core.CacheProcessor;
import cn.itmtx.ezcache.common.annotation.EzCache;
import cn.itmtx.ezcache.starter.autoconfigure.EzCacheProperties;
import cn.itmtx.ezcache.starter.aop.proxy.EzCacheAopProxy;
import cn.itmtx.ezcache.starter.utils.AopUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Advice
 * 对注解 {@link EzCache} 开启 AOP 方法拦截
 */
public class EzCacheAdvice implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(EzCacheAdvice.class);

    private final CacheProcessor cacheProcessor;

    private final EzCacheProperties ezCacheProperties;

    public EzCacheAdvice(CacheProcessor cacheProcessor, EzCacheProperties ezCacheProperties) {
        this.cacheProcessor = cacheProcessor;
        this.ezCacheProperties = ezCacheProperties;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (!this.ezCacheProperties.isEnable()) {
            return invocation.proceed();
        }

        Method method = invocation.getMethod();
        // 当前类可能是一个代理类，穿透代理，直接获取到实际的目标类的 Class 对象
        Class<?> cls = AopUtils.getTargetClass(invocation.getThis());
        // 如果 cls 不是最终的目标对象（可能是一个中间的代理），跳过当前代理继续向下执行，直到到达最终的目标对象
        if (!cls.equals(invocation.getThis().getClass())) {
            if (log.isDebugEnabled()) {
                log.debug(invocation.getThis().getClass() + "-->" + cls);
            }
            return invocation.proceed();
        }
        if (log.isDebugEnabled()) {
            log.debug(invocation.toString());
        }

        if (method.isAnnotationPresent(EzCache.class)) {
            EzCache ezCache = method.getAnnotation(EzCache.class);
            log.info(invocation.getThis().getClass().getName() + "." + method.getName() + "-->@EzCache");
            return cacheProcessor.process(new EzCacheAopProxy(invocation), ezCache);
        } else {
            // 在当前被调用的方法上没有找到 @EzCache 注解，但在该方法的重载版本或者父类中可能存在带有 @EzCache 注解
            Method specificMethod = org.springframework.aop.support.AopUtils.getMostSpecificMethod(method, invocation.getThis().getClass());
            if (specificMethod.isAnnotationPresent(EzCache.class)) {
                EzCache ezCache = specificMethod.getAnnotation(EzCache.class);
                log.info(invocation.getThis().getClass().getName() + "." + specificMethod.getName() + "-->@EzCache");
                return cacheProcessor.process(new EzCacheAopProxy(invocation), ezCache);
            }
        }

        return invocation.proceed();
    }
}
