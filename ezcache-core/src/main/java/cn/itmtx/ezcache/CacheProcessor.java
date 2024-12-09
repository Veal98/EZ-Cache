package cn.itmtx.ezcache;

import cn.itmtx.ezcache.annotation.EzCache;
import cn.itmtx.ezcache.bo.AutoRefreshBo;
import cn.itmtx.ezcache.bo.CacheKeyBo;
import cn.itmtx.ezcache.bo.CacheWrapper;
import cn.itmtx.ezcache.bo.ProcessingBo;
import cn.itmtx.ezcache.config.RefreshConfig;
import cn.itmtx.ezcache.enums.CacheOpTypeEnum;
import cn.itmtx.ezcache.lock.IDistributedLock;
import cn.itmtx.ezcache.parser.AbstractExpressionParser;
import cn.itmtx.ezcache.proxy.ICacheProxy;
import cn.itmtx.ezcache.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CacheProcessor {

    private static final Logger log = LoggerFactory.getLogger(CacheProcessor.class);

    /**
     * 正在处理中的 datasource 请求
     */
    public final ConcurrentHashMap<CacheKeyBo, ProcessingBo> datasourceProcessingMap;

    private final RefreshConfig refreshConfig;

    private final AbstractExpressionParser abstractExpressionParser;

    private final ICacheOperator cacheOperator;

    private final AutoRefreshProcessor autoRefreshProcessor;

    private final ActiveRefreshProcessor activeRefreshProcessor;

    /**
     * 分布式锁
     */
    private IDistributedLock distributedLock;

    /**
     * TODO 缓存变更监听器
     */
    private CacheChangeListener cacheChangeListener;

    public CacheProcessor(RefreshConfig refreshConfig, AbstractExpressionParser abstractExpressionParser, ICacheOperator cacheOperator, AutoRefreshProcessor autoRefreshProcessor, ActiveRefreshProcessor activeRefreshProcessor) {
        this.refreshConfig = refreshConfig;
        this.abstractExpressionParser = abstractExpressionParser;
        this.cacheOperator = cacheOperator;
        this.autoRefreshProcessor = autoRefreshProcessor;
        this.activeRefreshProcessor = activeRefreshProcessor;
        this.datasourceProcessingMap = new ConcurrentHashMap<>(refreshConfig.getProcessingMapSize());
    }

    /**
     * EzCache 注解处理，返回方法结果
     * @param proxy
     * @param ezCache
     * @return
     */
    public Object process(ICacheProxy proxy, EzCache ezCache) throws Throwable {
        Object[] args = proxy.getArgs();
        CacheOpTypeEnum cacheOpTypeEnum = getCacheOpTypeEnum(ezCache);
        log.info("CacheProcessor.process-->{}.{}--{})", proxy.getTarget().getClass().getName(), proxy.getMethod().getName(), cacheOpTypeEnum.name());

        // 从 Datasource 中读数据到更新到缓存中
        if (cacheOpTypeEnum.equals(CacheOpTypeEnum.DATASOURCE_LOAD)) {
            // 从 datasource 中获取数据，并写入缓存
            return loadDataFromDatasource(proxy, ezCache);
        } else if (cacheOpTypeEnum.equals(CacheOpTypeEnum.DATASOURCE_READ_ONLY)) {
            // 从 Datasource 中获取数据，不对缓存做任何操作
            return readDataFromDatasource(proxy);
        }

        // 1. 构建 cache key
        Method method = proxy.getMethod();
        CacheKeyBo cacheKeyBo = getCacheKeyBo(proxy, ezCache);
        if (null == cacheKeyBo) {
            // 如果 cache key 为空，则直接从 Datasource 中读数据，不对缓存做任何操作
            return readDataFromDatasource(proxy);
        }

        // 2. 从缓存中读取数据
        CacheWrapper<Object> cacheWrapper = cacheOperator.get(cacheKeyBo, method);
        log.info("cache key:{}, cache data is {} ", cacheKeyBo.getCacheKey(), cacheWrapper);

        if (CacheOpTypeEnum.CACHE_READ_ONLY.equals(cacheOpTypeEnum)) {
            // 若是从缓存中只读数据，走到这可以直接返回了
            return Objects.nonNull(cacheWrapper) ? cacheWrapper.getCacheObject() : null;
        }

        // 3.1 缓存中读到了数据并且没有过期
        if (null != cacheWrapper && !cacheWrapper.isExpired()) {
            // 往自动刷新队列中添加任务
            AutoRefreshBo autoRefreshBo = autoRefreshProcessor.putIfAbsent(cacheKeyBo, proxy, ezCache, cacheWrapper);
            if (null != autoRefreshBo) {
                // 更新自动刷新时间戳
                autoRefreshBo.flushRequestTime(cacheWrapper);
            } else {
                // 若不支持自动刷新, 则判断缓存是否快过期，若快过期则进行主动刷新
                activeRefreshProcessor.asyncRefresh(proxy, ezCache, cacheKeyBo, cacheWrapper);
            }
            return cacheWrapper.getCacheObject();
        }

        // 3.2 缓存中没有数据 或者 数据过期
        DataLoader dataLoader = new DataLoader();
        CacheWrapper<Object> newCacheWrapper = null;
        // 从 datasource 加载数据所花费的时间
        long loadDataTimeMillis = 0L;
        // 是否是并行缓存请求中的第一个
        boolean isFirst = false;
        try {
            dataLoader = dataLoader.init(proxy, cacheKeyBo, ezCache, this);
            newCacheWrapper = dataLoader.getData().getCacheWrapper();
            loadDataTimeMillis = dataLoader.getLoadDataTimeMills();
        } catch (Throwable e) {
            throw e;
        } finally {
            isFirst = dataLoader.isFirst();
        }

        if (isFirst) {
            // 往自动刷新队列中添加任务
            AutoRefreshBo autoRefreshBo = autoRefreshProcessor.putIfAbsent(cacheKeyBo, proxy, ezCache, newCacheWrapper);
            // 数据写入缓存
            this.reallyWriteCache(proxy, proxy.getArgs(), ezCache, cacheKeyBo, newCacheWrapper);
            if (null != autoRefreshBo) {
                // 更新自动刷新时间戳
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
    public void reallyWriteCache(ICacheProxy proxy, Object[] args, EzCache ezCache, CacheKeyBo cacheKeyBo, CacheWrapper<Object> cacheWrapper) throws Exception {
        if (null == cacheKeyBo) {
            return ;
        }

        Method method = proxy.getMethod();
        Object cacheObject = cacheWrapper.getCacheObject();
        long expireTimeMillis = cacheWrapper.getExpireMillis();

        try {
            if (!abstractExpressionParser.isCacheable(ezCache, proxy.getTarget(), args, cacheObject)) {
                return ;
            }
        } catch (Exception e) {
            throw e;
        }

        // 更新自动刷新的时间戳
        AutoRefreshBo autoRefreshBo = autoRefreshProcessor.getAutoRefreshBo(cacheKeyBo);
        if (null != autoRefreshBo && expireTimeMillis > 0) {
            autoRefreshBo.setExpireTimeMils(expireTimeMillis)
                    .setLastLoadTimeMillis(cacheWrapper.getLastLoadTimeMillis());
        }

        // 存入缓存
        cacheOperator.set(cacheKeyBo, cacheWrapper, method);
        // 缓存变更监听器
        if (null != cacheChangeListener) {
            cacheChangeListener.update(cacheKeyBo, cacheWrapper);
        }
    }

    /**
     * 生成缓存 KeyBo
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
     * @param proxy
     * @param cache
     * @param retVal 缓存数据
     * @return String 缓存Key
     */
    private CacheKeyBo getCacheKeyBo(ICacheProxy proxy, EzCache cache, Object retVal) {
        Object target = proxy.getTarget();
        String methodName = proxy.getMethod().getName();
        Object[] arguments = proxy.getArgs();
        String keyExpression = cache.key();
        return getCacheKeyBo(target, methodName, arguments, keyExpression, retVal, true);
    }

    /**
     * 生成缓存 KeyBo
     *
     * @param target           类名
     * @param methodName       方法名
     * @param arguments        参数
     * @param keyExpression    key表达式
     * @param retVal           缓存数据
     * @param hasResult        是否有返回值
     * @return CacheKeyTO
     */
    public CacheKeyBo getCacheKeyBo(Object target, String methodName, Object[] arguments, String keyExpression, Object retVal, boolean hasResult) {
        String key = null;
        if (null != keyExpression && keyExpression.trim().length() > 0) {
            // 优先解析 keyExpression
            try {
                key = abstractExpressionParser.getCacheKeyFromParseExpression(keyExpression, target, arguments, retVal, hasResult);
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

        return new CacheKeyBo(refreshConfig.getNamespace(), key);
    }

    /**
     * Datasource 中获取数据，不对缓存做任何操作
     * @param proxy
     * @return
     */
    private Object readDataFromDatasource(ICacheProxy proxy) {
        return null;
    }

    /**
     * 从 datasource 中获取数据，并写入缓存
     * 注意，这里不要使用 "在途读"请求缓冲区, 因为当前可能是更新数据的方法
     * @param proxy
     * @param ezCache
     * @return
     */
    private Object loadDataFromDatasource(ICacheProxy proxy, EzCache ezCache) throws Throwable {

        DataLoader dataLoader = new DataLoader();
        CacheWrapper<Object> cacheWrapper;
        try {
            // 从 datasource 获取数据
            cacheWrapper = dataLoader.init(proxy, ezCache, this).getData().getCacheWrapper();
        } catch (Throwable e) {
            throw e;
        }

        Object cacheObject = cacheWrapper.getCacheObject();
        Object[] args = proxy.getArgs();

        if (abstractExpressionParser.isCacheable(ezCache, proxy.getTarget(), args, cacheObject)) {
            CacheKeyBo cacheKeyBo = getCacheKeyBo(proxy, ezCache, cacheObject);
            // 注意：这里只能获取 autoRefreshBo，不能生成 autoRefreshBo (autoRefreshProcessor.putIfAbsent)
            AutoRefreshBo autoRefreshBo = autoRefreshProcessor.getAutoRefreshBo(cacheKeyBo);
            try {
                // 写入缓存
                this.reallyWriteCache(proxy, proxy.getArgs(), ezCache, cacheKeyBo, cacheWrapper);
                if (null != autoRefreshBo) {
                    // 同步加载时间
                    autoRefreshBo.setLastLoadTimeMillis(cacheWrapper.getLastLoadTimeMillis())
                            // 同步过期时间
                            .setExpireTimeMils(cacheWrapper.getExpireMillis());
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return cacheObject;
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

    public IDistributedLock getDistributedLock() {
        return distributedLock;
    }

    public void setDistributedLock(IDistributedLock distributedLock) {
        this.distributedLock = distributedLock;
    }

    public CacheWrapper<Object> getCacheWrapper(CacheKeyBo cacheKeyBo, Method method) {
        return cacheOperator.get(cacheKeyBo, method);
    }

    public CacheChangeListener getCacheChangeListener() {
        return cacheChangeListener;
    }

    public void setCacheChangeListener(CacheChangeListener cacheChangeListener) {
        this.cacheChangeListener = cacheChangeListener;
    }
}
