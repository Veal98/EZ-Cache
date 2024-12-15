package cn.itmtx.ezcache.starter.utils;

import org.springframework.aop.framework.AopProxyUtils;

/**
 *
 */
public class AopUtils {

    /**
     * target 可能是一个代理类，此方法用于穿透代理，直接获取到实际的目标类的 Class 对象
     * @param target
     * @return
     */
    public static Class<?> getTargetClass(Object target) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        if (targetClass == null && target != null) {
            targetClass = target.getClass();
        }
        return targetClass;
    }

}
