package cn.itmtx.ezcache;

import cn.itmtx.ezcache.annotation.EzCache;
import cn.itmtx.ezcache.bo.AutoRefreshBo;
import cn.itmtx.ezcache.bo.CacheKeyBo;
import cn.itmtx.ezcache.bo.CacheWrapper;
import cn.itmtx.ezcache.config.AutoLoadConfig;
import cn.itmtx.ezcache.enums.CacheOpTypeEnum;
import cn.itmtx.ezcache.parser.ExpressionParser;
import cn.itmtx.ezcache.proxy.ICacheProxy;
import cn.itmtx.ezcache.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class CacheProcessor {

    private static final Logger log = LoggerFactory.getLogger(CacheProcessor.class);

    private final AutoLoadConfig autoLoadConfig;

    private final ExpressionParser expressionParser;

    private final ICacheOperator cacheOperator;

    private final AutoRefreshProcessor autoRefreshProcessor;

    private final ActiveRefreshProcessor activeRefreshProcessor;

    public CacheProcessor(AutoLoadConfig autoLoadConfig, ExpressionParser expressionParser, ICacheOperator cacheOperator, AutoRefreshProcessor autoRefreshProcessor, ActiveRefreshProcessor activeRefreshProcessor) {
        this.autoLoadConfig = autoLoadConfig;
        this.expressionParser = expressionParser;
        this.cacheOperator = cacheOperator;
        this.autoRefreshProcessor = autoRefreshProcessor;
        this.activeRefreshProcessor = activeRefreshProcessor;
    }

    /**
     * EzCache 注解处理，返回方法结果
     * @param proxy
     * @param ezCache
     * @return
     */
    public Object process(ICacheProxy proxy, EzCache ezCache) {
        Object[] args = proxy.getArgs();
        CacheOpTypeEnum cacheOpTypeEnum = getCacheOpTypeEnum(ezCache);
        log.info("CacheProcessor.process-->{}.{}--{})", proxy.getTarget().getClass().getName(), proxy.getMethod().getName(), cacheOpTypeEnum.name());

        // 从 DB 中读数据到更新到缓存中
        if (cacheOpTypeEnum.equals(CacheOpTypeEnum.DATASOURCE_LOAD)) {
            return loadData(proxy, ezCache);
        } else if (cacheOpTypeEnum.equals(CacheOpTypeEnum.DATASOURCE_READ_ONLY)) {
            // 直接从 DB 中读数据，不对缓存做任何操作
            return readData(proxy);
        }

        Method method = proxy.getMethod();
        CacheKeyBo cacheKeyBo = getCacheKeyBo(proxy, ezCache);
        if (null == cacheKeyBo) {
            // 如果 cache key 为空，则直接从 DB 中读数据，不对缓存做任何操作
            return readData(proxy);
        }

        // 从缓存中读取数据
        CacheWrapper<Object> cacheWrapper = cacheOperator.get(cacheKeyBo, method);
        log.info("cache key:{}, cache data is {} ", cacheKeyBo.getKey(), cacheWrapper);

        // 缓存中读到了数据并且没有过期
        if (null != cacheWrapper && !cacheWrapper.isExpired()) {
            // 获取自动刷新相关信息
            AutoRefreshBo autoRefreshBo = autoRefreshProcessor.getAutoLoadTO(cacheKeyBo);
            if (null != autoRefreshBo) {
                // 更新自动刷新时间戳
                autoRefreshBo.flushRequestTime(cacheWrapper);
            } else {
                // 若没有自动刷新信息, 则判断缓存是否快过期，若快过期则进行主动刷新
                activeRefreshProcessor.asyncRefresh(proxy, ezCache, cacheKeyBo, cacheWrapper);
            }
            return cacheWrapper.getCacheObject();
        }

        // 缓存中没有数据 或者 数据过期
        DataLoader dataLoader = new DataLoader();
        CacheWrapper<Object> newCacheWrapper = null;
        // 从 datasource 加载数据所花费的时间
        long loadDataTimeMillis = 0L;
        // 是否是并行缓存请求中的第一个
        boolean isFirst = false;
        try {
            dataLoader = dataLoader.init(proxy, cacheKeyBo, ezCache, this);
            newCacheWrapper = dataLoader.loadData().getCacheWrapper();
            loadDataTimeMillis = dataLoader.getLoadDataTimeMills();
        } finally {
            isFirst = dataLoader.isFirst();
        }

        if (isFirst) {
            // 往自动加载队列中添加任务
            AutoRefreshBo autoRefreshBo = autoRefreshProcessor.putIfAbsent(cacheKeyBo, proxy, ezCache, newCacheWrapper);
            // 数据写入缓存
            this.writeCache(proxy, proxy.getArgs(), ezCache, cacheKeyBo, newCacheWrapper);
            if (null != autoRefreshBo) {
                // 更新自动加载时间戳
                autoRefreshBo.flushRequestTime(newCacheWrapper);
                autoRefreshBo.addTotalLoadDataTime(loadDataTimeMillis);
            }
        }

        return newCacheWrapper.getCacheObject();
    }

    /**
     * 数据写入缓存
     * @param proxy
     * @param args
     * @param ezCache
     * @param cacheKeyBo
     * @param cacheWrapper
     */
    public void writeCache(ICacheProxy proxy, Object[] args, EzCache ezCache, CacheKeyBo cacheKeyBo, CacheWrapper<Object> cacheWrapper) {
        return ;
    }

    /**
     * 生成缓存 Key
     *
     * @param proxy
     * @param cache
     * @return String 缓存Key
     */
    private CacheKeyBo getCacheKeyBo(ICacheProxy proxy, EzCache cache) {
        Object target = proxy.getTarget();
        String methodName = proxy.getMethod().getName();
        Object[] arguments = proxy.getArgs();
        String keyExpression = cache.key();
        return getCacheKeyBo(target, methodName, arguments, keyExpression, null, false);
    }

    /**
     * 生成缓存 KeyBo
     *
     * @param target           类名
     * @param methodName       方法名
     * @param arguments        参数
     * @param keyExpression    key表达式
     * @param result           执行实际方法的返回值
     * @param hasResult        是否有返回值
     * @return CacheKeyTO
     */
    private CacheKeyBo getCacheKeyBo(Object target, String methodName, Object[] arguments, String keyExpression, Object result, boolean hasResult) {
        String key = null;
        if (null != keyExpression && keyExpression.trim().length() > 0) {
            // 优先解析 keyExpression
            try {
                key = expressionParser.getCacheKeyFromParseExpression(keyExpression, target, arguments, result, hasResult);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            // 默认 cache key
            key = CacheUtils.getDefaultCacheKey(target.getClass().getName(), methodName, arguments);
        }

        if (null == key || key.trim().isEmpty()) {
            throw new IllegalArgumentException("cache key for " + target.getClass().getName() + "." + methodName + " is empty");
        }

        return new CacheKeyBo(autoLoadConfig.getNamespace(), key);
    }

    private Object readData(ICacheProxy proxy) {
        return null;
    }

    private Object loadData(ICacheProxy proxy, EzCache cache) {
        return null;
    }

    /**
     * 获取CacheOpType: 从Cache注解中获取
     *
     * @param cache     注解
     * @return CacheOpType
     */
    private CacheOpTypeEnum getCacheOpTypeEnum(EzCache cache) {
        // 从 EzCache 注解中解析 opType
        CacheOpTypeEnum operationTypeEnum = cache.operationType();
        if (null == operationTypeEnum) {
            operationTypeEnum = CacheOpTypeEnum.CACHE_READ_DATASOURCE_LOAD;
        }
        return operationTypeEnum;
    }
}
