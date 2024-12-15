package cn.itmtx.ezcache.starter.config;

import cn.itmtx.ezcache.lock.IDistributedLock;
import cn.itmtx.ezcache.operator.ICacheOperator;
import cn.itmtx.ezcache.operator.map.ConcurrentHashMapCacheOperator;
import cn.itmtx.ezcache.parser.IExpressionParser;
import cn.itmtx.ezcache.parser.SpringElExpressionParser;
import cn.itmtx.ezcache.serializer.ISerializer;
import cn.itmtx.ezcache.serializer.hessian.HessianSerializer;
import cn.itmtx.ezcache.starter.autoconfigure.EzCacheProperties;
import cn.itmtx.ezcache.starter.redis.SpringRedisDistributedLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 自定义自动加载配置
 */
@Configuration
public class EzCacheCustomAutoConfiguration {

    @Bean
    @Primary
    public ICacheOperator cacheOperator(EzCacheProperties ezCacheProperties) {
        return new ConcurrentHashMapCacheOperator(ezCacheProperties.getCacheConfigBo());
    }

//    @Bean
//    @Primary
//    @ConditionalOnMissingBean(ICacheOperator.class)
//    @ConditionalOnClass(ISerializer.class)
//    @ConditionalOnBean(ISerializer.class)
//    public ICacheOperator cacheOperator(ISerializer<Object> serializer) {
//        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
//        return new SpringRedisCacheOperator(connectionFactory, serializer);
//    }

    @Bean
    @Primary
    public ISerializer<Object> serializer() {
        return new HessianSerializer();
    }

    @Bean
    @Primary
    public IExpressionParser expressionParser() {
        return new SpringElExpressionParser();
    }

    @Bean
    @Primary
    public IDistributedLock distributedLock(RedisConnectionFactory connectionFactory) {
        if (null == connectionFactory) {
            return null;
        }

        return new SpringRedisDistributedLock(connectionFactory);
    }

}
