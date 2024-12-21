package cn.itmtx.ezcache.starter.autoconfigure;

import cn.itmtx.ezcache.operator.ICacheOperator;
import cn.itmtx.ezcache.parser.IExpressionParser;
import cn.itmtx.ezcache.parser.SpringElExpressionParser;
import cn.itmtx.ezcache.serializer.ISerializer;
import cn.itmtx.ezcache.serializer.hessian.HessianSerializer;
import cn.itmtx.ezcache.starter.redis.SpringRedisCacheOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author jc.yin
 * @Date 2024/12/10
 * @Description 自动配置类（默认配置, 定义表达式解析器, 序列化器, 缓存操作器的实现方式）, 若需要自定义新建配置类进行覆盖即可 {@see EzCacheCustomAutoConfiguration}
 **/
@Configuration
@ConditionalOnClass(ICacheOperator.class)
@EnableConfigurationProperties(EzCacheProperties.class)
@AutoConfigureAfter(RedisAutoConfiguration.class) // 在 redis 之后再自动加载（分布式锁依赖于 redis 的实现）
@ConditionalOnProperty(value = EzCacheProperties.PREFIX + ".enable", matchIfMissing = true) // 自动加载条件
public class EzCacheDefaultAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(EzCacheDefaultAutoConfiguration.class);

    /**
     * 默认用 SpringElExpressionParser 实现表达式解析
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(IExpressionParser.class)
    public IExpressionParser ezCacheExpressionParser() {
        log.info("IExpressionParser with SpringElExpressionParser auto-configured");
        return new SpringElExpressionParser();
    }

    /**
     * 默认用 HessianSerializer 实现序列化
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ISerializer.class)
    public ISerializer<Object> ezCacheSerializer() {
        log.info("ISerializer with HessianSerializer auto-configured");
        return new HessianSerializer();
    }

    /**
     * 默认用 Spring Redis 实现缓存操作
     * @param serializer
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ICacheOperator.class)
    @ConditionalOnClass(ISerializer.class)
    @ConditionalOnBean(ISerializer.class)
    public ICacheOperator ezCacheCacheCacheOperator(ISerializer<Object> serializer) {
        return new SpringRedisCacheOperator(serializer);
    }
}
