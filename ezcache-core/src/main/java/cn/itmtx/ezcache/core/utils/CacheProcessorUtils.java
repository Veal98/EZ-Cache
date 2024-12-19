package cn.itmtx.ezcache.core.utils;

import cn.itmtx.ezcache.common.annotation.EzCache;
import cn.itmtx.ezcache.common.bo.CacheKeyBo;
import cn.itmtx.ezcache.common.bo.EzCacheConfig;
import cn.itmtx.ezcache.common.enums.ErrorCodeEnum;
import cn.itmtx.ezcache.common.exception.EzCacheCommonException;
import cn.itmtx.ezcache.common.utils.EzCacheUtils;
import cn.itmtx.ezcache.core.proxy.ICacheProxy;
import cn.itmtx.ezcache.parser.IExpressionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheProcessorUtils {

    private static final Logger log = LoggerFactory.getLogger(CacheProcessorUtils.class);

    /**
     * 生成缓存 KeyBo
     *
     * @param proxy
     * @param cache
     * @return String 缓存Key
     */
    public static CacheKeyBo getCacheKeyBo(IExpressionParser expressionParser, EzCacheConfig ezCacheConfig, ICacheProxy proxy, EzCache cache) throws EzCacheCommonException {
        Object target = proxy.getTarget();
        String methodName = proxy.getMethod().getName();
        Object[] arguments = proxy.getArgs();
        String keyExpression = cache.key();
        return doGetCacheKeyBo(expressionParser, ezCacheConfig, target, methodName, arguments, keyExpression, null, false);
    }

    /**
     * 生成缓存 KeyBo
     *
     * @param proxy
     * @param cache
     * @param retVal 缓存数据
     * @return String 缓存Key
     */
    public static CacheKeyBo getCacheKeyBo(IExpressionParser expressionParser, EzCacheConfig ezCacheConfig, ICacheProxy proxy, EzCache cache, Object retVal) throws EzCacheCommonException {
        Object target = proxy.getTarget();
        String methodName = proxy.getMethod().getName();
        Object[] arguments = proxy.getArgs();
        String keyExpression = cache.key();
        return doGetCacheKeyBo(expressionParser, ezCacheConfig, target, methodName, arguments, keyExpression, retVal, true);
    }


    private static CacheKeyBo doGetCacheKeyBo(IExpressionParser expressionParser, EzCacheConfig ezCacheConfig, Object target, String methodName, Object[] arguments, String keyExpression, Object retVal, boolean hasResult) throws EzCacheCommonException {
        String key;
        if (null != keyExpression && !keyExpression.trim().isEmpty()) {
            // 优先解析 keyExpression
            try {
                key = expressionParser.parseCacheKeyFromExpression(keyExpression, target, arguments, retVal, hasResult);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new EzCacheCommonException(ErrorCodeEnum.KEY_EXPRESSION_PARSE_ERROR, e);
            }
        } else {
            // 默认 cache key
            key = EzCacheUtils.getDefaultCacheKey(target.getClass().getName(), methodName, arguments);
        }

        if (null == key || key.trim().isEmpty()) {
            throw new EzCacheCommonException(ErrorCodeEnum.KEY_EXPRESSION_PARSE_ERROR);
        }

        return new CacheKeyBo(ezCacheConfig.getNamespace(), key);
    }
}
