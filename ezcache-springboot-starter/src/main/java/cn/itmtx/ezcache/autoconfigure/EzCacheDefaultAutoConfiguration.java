package cn.itmtx.ezcache.autoconfigure;

import cn.itmtx.ezcache.cacher.ICacheOperator;
import cn.itmtx.ezcache.cacher.redis.AbstractRedisCacheOperator;
import cn.itmtx.ezcache.cacher.redis.JedisClusterCacheOperator;
import cn.itmtx.ezcache.parser.AbstractExpressionParser;
import cn.itmtx.ezcache.parser.SpringElExpressionParser;
import cn.itmtx.ezcache.redis.SpringRedisCacheOperator;
import cn.itmtx.ezcache.serializer.HessianSerializer;
import cn.itmtx.ezcache.serializer.ISerializer;
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
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * @Author jc.yin
 * @Date 2024/12/10
 * @Description
 **/
@Configuration
@ConditionalOnClass(ICacheOperator.class)
@EnableConfigurationProperties(EzCacheProperties.class)
@AutoConfigureAfter(RedisAutoConfiguration.class) // 在 redis 之后再自动加载（分布式锁依赖于 redis 的实现）
@ConditionalOnProperty(value = "ezcache.enable", matchIfMissing = true) // 自动加载条件
public class EzCacheDefaultAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(EzCacheDefaultAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(AbstractExpressionParser.class)
    public AbstractExpressionParser autoLoadExpressionParser() {
        return new SpringElExpressionParser();
    }

    @Bean
    @ConditionalOnMissingBean(ISerializer.class)
    public ISerializer<Object> autoLoadSerializer() {
        return new HessianSerializer();
    }

    @Configuration
    @ConditionalOnClass(Jedis.class)
    static class JedisCacheOperatorConfiguration {
        @Bean
        @ConditionalOnMissingBean(ICacheOperator.class)
        @ConditionalOnBean(JedisConnectionFactory.class)
        public ICacheOperator autoLoadCacheOperator(EzCacheProperties properties, ISerializer<Object> serializer, JedisConnectionFactory jedisConnectionFactory) {
            RedisConnection redisConnection = null;
            try {
                redisConnection = jedisConnectionFactory.getConnection();
                AbstractRedisCacheOperator cacheOperator = null;
                if (redisConnection instanceof JedisClusterConnection) {
                    JedisClusterConnection redisClusterConnection = (JedisClusterConnection) redisConnection;
                    // 优先使用JedisCluster; 因为JedisClusterConnection 批量处理，需要使用JedisCluster
                    JedisCluster jedisCluster = redisClusterConnection.getNativeConnection();
                    cacheOperator = new JedisClusterCacheOperator(jedisCluster, serializer);
                } else {
                    // 默认使用 Spring Redis
                    cacheOperator = new SpringRedisCacheOperator(serializer, jedisConnectionFactory);
                }
                return cacheOperator;
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
                throw e;
            } finally {
                RedisConnectionUtils.releaseConnection(redisConnection, jedisConnectionFactory);
            }
        }
    }

}
