package cn.itmtx.ezcache.starter.autoconfigure;

import cn.itmtx.ezcache.lock.IDistributedLock;
import cn.itmtx.ezcache.starter.redis.SpringRedisDistributedLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@AutoConfigureAfter({EzCacheDefaultAutoConfiguration.class})
public class DistributedLockAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DistributedLockAutoConfiguration.class);

    /**
     * 默认用 Spring Redis 实现分布式锁
     * @param connectionFactory
     * @return
     */
    @Bean
    @ConditionalOnMissingBean({IDistributedLock.class})
    @ConditionalOnClass(RedisConnectionFactory.class)
    @ConditionalOnBean(RedisConnectionFactory.class)
    public IDistributedLock ezCacheDistributedLock(RedisConnectionFactory connectionFactory) {
        if (null == connectionFactory) {
            return null;
        }

        SpringRedisDistributedLock lock = new SpringRedisDistributedLock(connectionFactory);
        logger.info("IDistributedLock with SpringRedisDistributedLock auto-configured");
        return lock;
    }
}
