package cn.itmtx.ezcache.starter.aop.advisor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

import java.lang.annotation.Annotation;

/**
 * 自定义 Advisor
 */
public class MethodAnnotationPointcutAdvisor extends AbstractPointcutAdvisor {

    /**
     * 切点
     */
    private final Pointcut pointcut;

    /**
     * 通知
     */
    private final Advice advice;

    public MethodAnnotationPointcutAdvisor(Class<? extends Annotation> methodAnnotationType, Advice advice) {
        this.pointcut = new AnnotationMatchingPointcut(null, methodAnnotationType);
        this.advice = advice;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }
}
