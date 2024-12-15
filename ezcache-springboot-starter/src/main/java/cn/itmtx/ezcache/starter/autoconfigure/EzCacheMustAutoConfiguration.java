package cn.itmtx.ezcache.starter.autoconfigure;

import cn.itmtx.ezcache.common.annotation.EzCache;
import cn.itmtx.ezcache.common.annotation.EzCacheDelete;
import cn.itmtx.ezcache.core.CacheProcessor;
import cn.itmtx.ezcache.lock.IDistributedLock;
import cn.itmtx.ezcache.operator.ICacheOperator;
import cn.itmtx.ezcache.parser.IExpressionParser;
import cn.itmtx.ezcache.serializer.ISerializer;
import cn.itmtx.ezcache.starter.aop.advice.EzCacheAdvice;
import cn.itmtx.ezcache.starter.aop.advice.EzCacheDeleteAdvice;
import cn.itmtx.ezcache.starter.aop.advisor.MethodAnnotationPointcutAdvisor;
import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * @Author jc.yin
 * @Date 2024/12/11
 * @Description 自动配置类（必须要加载的配置, 主要是 AOP 相关配置）, 需要先完成 {@link EzCacheDefaultAutoConfiguration}
 **/
@Configuration
@ConditionalOnClass(CacheProcessor.class)
@AutoConfigureAfter({EzCacheDefaultAutoConfiguration.class, DistributedLockAutoConfiguration.class})
@ConditionalOnProperty(value = EzCacheProperties.PREFIX + ".enable", matchIfMissing = true) // 自动加载条件
public class EzCacheMustAutoConfiguration {

    @Autowired
    private EzCacheProperties ezCacheProperties;

    private final IDistributedLock distributedLock;

    public EzCacheMustAutoConfiguration(ObjectProvider<IDistributedLock> distributedLockObjectProvider) {
        if (null != distributedLockObjectProvider) {
            distributedLock = distributedLockObjectProvider.getIfAvailable();
        } else {
            distributedLock = null;
        }
    }

    /**
     * EzCache 自动加载必要性检测
     * @return
     */
    @Bean
    public EzCacheValidator ezCacheAutoConfigurationValidator() {
        return new EzCacheValidator();
    }

    /**
     * 自动加载 CacheProcessor
     * @param cacheOperator
     * @param expressionParser
     * @return
     */
    @Bean(destroyMethod = "destroy") // 当Spring容器关闭时，调用 CacheProcessor 实例的 destroy 方法来执行清理工作
    @ConditionalOnMissingBean(CacheProcessor.class)
    @ConditionalOnBean({ ICacheOperator.class, IExpressionParser.class })
    public CacheProcessor cacheProcessor(ICacheOperator cacheOperator, IExpressionParser expressionParser) {
        CacheProcessor cacheProcessor = new CacheProcessor(cacheOperator, expressionParser, ezCacheProperties.getCacheConfigBo());
        cacheProcessor.setDistributedLock(distributedLock);
        return cacheProcessor;
    }

    /**
     * 1. 配置 EzCache 注解 Advice
     * @param cacheProcessor
     * @return
     */
    @Bean
    @ConditionalOnBean(CacheProcessor.class)
    @ConditionalOnProperty(value = EzCacheProperties.PREFIX + ".enable-readwrite", matchIfMissing = true)
    public EzCacheAdvice ezCacheAdvice(CacheProcessor cacheProcessor) {
        return new EzCacheAdvice(cacheProcessor, ezCacheProperties);
    }

    /**
     * 配置 EzCacheDelete 注解 Advice
     * @param cacheProcessor
     * @return
     */
    @Bean
    @ConditionalOnBean(CacheProcessor.class)
    @ConditionalOnProperty(value = EzCacheProperties.PREFIX + ".enable-delete", matchIfMissing = true)
    public EzCacheDeleteAdvice ezCacheDeleteAdvice(CacheProcessor cacheProcessor) {
        return new EzCacheDeleteAdvice(cacheProcessor, ezCacheProperties);
    }

    /**
     * 2. 配置 EzCache 注解 Advisor
     * @param ezCacheAdvice
     * @return
     */
    @Bean
    @ConditionalOnBean(EzCacheAdvice.class)
    public AbstractPointcutAdvisor ezCacheAdvisor(EzCacheAdvice ezCacheAdvice) {
        AbstractPointcutAdvisor cacheAdvisor = new MethodAnnotationPointcutAdvisor(EzCache.class, ezCacheAdvice);
        cacheAdvisor.setOrder(ezCacheProperties.getAopOrder());
        return cacheAdvisor;
    }

    /**
     * 配置 EzCacheDelete 注解 Advisor
     * @param ezCacheDeleteAdvice
     * @return
     */
    @Bean
    @ConditionalOnBean(EzCacheAdvice.class)
    public AbstractPointcutAdvisor ezCacheDeleteAdvisor(EzCacheDeleteAdvice ezCacheDeleteAdvice) {
        AbstractPointcutAdvisor cacheAdvisor = new MethodAnnotationPointcutAdvisor(EzCacheDelete.class, ezCacheDeleteAdvice);
        cacheAdvisor.setOrder(ezCacheProperties.getDeleteAopOrder());
        return cacheAdvisor;
    }

    /**
     * 3. 配置用于创建代理对象的组件
     */
    @Bean
    @ConditionalOnBean(CacheProcessor.class)
    public AbstractAdvisorAutoProxyCreator autoloadCacheAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxy = new DefaultAdvisorAutoProxyCreator();
        proxy.setAdvisorBeanNamePrefix("ezCache");
        proxy.setProxyTargetClass(ezCacheProperties.isCglibProxyTargetClass());
        return proxy;
    }


    static class EzCacheValidator {
        @Autowired(required = false)
        private IExpressionParser expressionParser;

        @Autowired(required = false)
        private ISerializer<Object> serializer;

        @Autowired(required = false)
        private ICacheOperator cacheOperator;

        @PostConstruct
        public void checkHasCacheManager() {
            Assert.notNull(this.expressionParser, "No expressionParser could be auto-configured!");
            Assert.notNull(this.serializer, "No serializer could be auto-configured!");
            Assert.notNull(this.cacheOperator, "No cacheOperator could be auto-configured!");
        }
    }
}
