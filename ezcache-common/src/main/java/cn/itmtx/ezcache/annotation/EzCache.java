package cn.itmtx.ezcache.annotation;

import cn.itmtx.ezcache.enums.CacheOpTypeEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface EzCache {

    /**
     * 自定义缓存 Key，支持表达式
     *
     * @return String
     */
    String key();

    /**
     * 缓存的条件表达式
     * 若为 false 则不进行缓存, true 或者空进行缓存
     *
     * @return String
     */
    String condition() default "";

    /**
     * 缓存的过期时间，单位：毫秒，如果为 0 则表示永久缓存
     *
     * @return 时间
     */
    long expireTimeMillis();

    /**
     * 缓存过期时间的表达式
     * 优先使用 expireExp，若其执行结果为null或小于0时使用expire
     *
     * @return 时间
     */
    String expireExp() default "";

    /**
     * 缓存自动刷新时间(单位：毫秒)
     * - 必须满足 0 < autoLoadTimeMillis < expireTimeMillis 才有效
     * - 若缓存在 autoLoadTimeMillis 时间内即将过期，则会自动更新缓存内容
     *
     * @return 时间
     */
    long autoLoadTimeMillis() default 0L;

    /**
     * 当 autoLoad 为 true 时，若缓存数据在 tolerateTimeMillis 之内没有使用，则不再进行缓存自动刷新
     * 如果 tolerateTimeMillis 为 0，则会一直自动刷新
     * 默认 tolerateTimeMillis = 24小时
     * @return long 请求过期
     */
    long autoLoadTolerateTimeMillis() default 86400000L;

    /**
     * 缓存的操作类型：默认是 CACHE_READ_DATASOURCE_LOAD
     * @return
     */
    CacheOpTypeEnum operationType() default CacheOpTypeEnum.CACHE_READ_DATASOURCE_LOAD;

    /**
     * 并发等待时间(毫秒),等待正在从 datasource 加载数据的线程返回的等待时间
     * 默认 1s
     * @return 时间
     */
    long concurrentWaitTimeMillis() default 1000L;


}
