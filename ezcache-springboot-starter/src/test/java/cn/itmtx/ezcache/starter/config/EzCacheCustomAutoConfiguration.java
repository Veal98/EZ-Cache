package cn.itmtx.ezcache.starter.config;

import cn.itmtx.ezcache.lock.IDistributedLock;
import cn.itmtx.ezcache.operator.ICacheOperator;
import cn.itmtx.ezcache.parser.IExpressionParser;
import cn.itmtx.ezcache.parser.SpringElExpressionParser;
import cn.itmtx.ezcache.serializer.ISerializer;
import cn.itmtx.ezcache.serializer.hessian.HessianSerializer;
import cn.itmtx.ezcache.starter.redis.SpringRedisCacheOperator;
import cn.itmtx.ezcache.starter.redis.SpringRedisDistributedLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * 自定义自动加载配置
 */
@Configuration
public class EzCacheCustomAutoConfiguration {

//    @Bean
//    @Primary
//    public ICacheOperator cacheOperator(EzCacheProperties ezCacheProperties) {
//        return new ConcurrentHashMapCacheOperator(ezCacheProperties.getCacheConfigBo());
//    }

    /**
     * @param serializer
     * @return
     */
    @Bean
    @Primary
    public ICacheOperator cacheOperator(ISerializer<Object> serializer) {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.afterPropertiesSet();
        return new SpringRedisCacheOperator(jedisConnectionFactory, serializer);
    }

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
