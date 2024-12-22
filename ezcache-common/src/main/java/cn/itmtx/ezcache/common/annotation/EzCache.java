package cn.itmtx.ezcache.common.annotation;

import cn.itmtx.ezcache.common.enums.CacheOpTypeEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface EzCache {

    /**
     * 缓存的操作类型：默认是 CACHE_READ_DATASOURCE_LOAD
     * @return
     */
    CacheOpTypeEnum operationType() default CacheOpTypeEnum.CACHE_READ_DATASOURCE_LOAD;

    /**
     * 自定义缓存 Key，支持表达式
     *
     * @return String
     */
    String key();

    /**
     * 缓存的过期时间，单位：毫秒，如果为 0 则表示永久缓存
     *
     * @return 时间
     */
    long expireTimeMillis();

    /**
     * 是否启用缓存的条件表达式
     * 若为 false 则不进行缓存, true 或者空进行缓存
     * 也可使用表达式构造复杂的条件
     *
     * @return String
     */
    String condition() default "";

    /**
     * 是否开启自动刷新的条件表达式
     * 若为 false 则不进行自动刷新, true 或者空进行自动刷新
     * 也可使用表达式构造复杂的条件
     * @return
     */
    String autoRefreshCondition() default "";

    /**
     * 若开启自动刷新，缓存数据持续 autoRefreshNoRequestTimeoutMillis(单位：毫秒) 没有被使用，就关闭对此缓存数据的自动刷新
     * 如果 autoRefreshNoRequestTimeoutMillis 为 0 时，自动刷新会一直开启
     *
     * @return
     */
    long autoRefreshNoRequestTimeoutMillis() default 0L;

    /**
     * 预警缓存刷新时间(单位：秒)
     * 必须满足 0 < refreshAlarmTimeMillis < expire 才有效
     * 当缓存在 refreshAlarmTimeMillis 时间内即将过期的话，则刷新缓存内容
     * @return long 请求过期
     */
    long refreshAlarmTimeMillis() default 86400000L;

    /**
     * 并发等待时间(毫秒),等待正在从 datasource 加载数据的线程返回的等待时间
     * 默认 1s
     * @return 时间
     */
    long waitDatasourceTimeoutMillis() default 1000L;

    /**
     * 尝试获取分布式锁的时间（单位：毫秒, 默认 10s），在设置分布式锁的前提下，如果此项值大于0，则会使用分布式锁，如果小于等于0，则不会使用分布式锁。
     *
     * @return 分布式锁的缓存时间
     */
    long distributedLockTimeoutMillis() default 10000;

    /**
     * 是否开启锁降级
     * 默认不开启;
     * 如果开启，当分布式锁抛异常时不使用分布式锁
     */
    boolean openDistributedLockDown() default false;

}
