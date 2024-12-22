package cn.itmtx.ezcache.starter.config;

import cn.itmtx.ezcache.lock.IDistributedLock;
import cn.itmtx.ezcache.operator.ICacheOperator;
import cn.itmtx.ezcache.parser.IExpressionParser;
import cn.itmtx.ezcache.parser.SpringElExpressionParser;
import cn.itmtx.ezcache.serializer.ISerializer;
import cn.itmtx.ezcache.serializer.hessian.HessianSerializer;
import cn.itmtx.ezcache.serializer.jdk.JdkSerializer;
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
     * 指定缓存操作器
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

    /**
     * 指定序列化器
     * @return
     */
    @Bean
    @Primary
    public ISerializer<Object> serializer() {
        return new JdkSerializer();
    }

    /**
     * 指定表达式解析器
     * @return
     */
    @Bean
    @Primary
    public IExpressionParser expressionParser() {
        return new SpringElExpressionParser();
    }

    /**
     * 指定分布式锁实现
     * @param connectionFactory
     * @return
     */
    @Bean
    @Primary
    public IDistributedLock distributedLock(RedisConnectionFactory connectionFactory) {
        if (null == connectionFactory) {
            return null;
        }

        return new SpringRedisDistributedLock(connectionFactory);
    }

}
