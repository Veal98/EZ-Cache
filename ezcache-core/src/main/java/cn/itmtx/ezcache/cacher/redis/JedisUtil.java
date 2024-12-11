package cn.itmtx.ezcache.cacher.redis;

import cn.itmtx.ezcache.bo.CacheKeyBo;
import com.jarvis.cache.MSetParam;
import com.jarvis.cache.to.CacheKeyTO;
import com.jarvis.cache.to.CacheWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.PipelineBase;

import java.util.Collection;
import java.util.Set;

public class JedisUtil {

    private static final Logger log = LoggerFactory.getLogger(JedisUtil.class);
    

    public static void executeMGet(PipelineBase pipeline, Set<CacheKeyBo> keys) {
        String hfield;
        String cacheKey;
        byte[] key;
        for (CacheKeyBo cacheKeyBo : keys) {
            cacheKey = cacheKeyBo.getCacheKey();
            if (null == cacheKey || cacheKey.isEmpty()) {
                continue;
            }
            key = AbstractRedisCacheOperator.KEY_SERIALIZER.serialize(cacheKey);
            pipeline.get(key);
        }
    }

    public static void executeDelete(PipelineBase pipeline, Set<CacheKeyBo> keys) {
        String hfield;
        String cacheKey;
        byte[] key;
        for (CacheKeyBo cacheKeyBo : keys) {
            cacheKey = cacheKeyBo.getCacheKey();
            if (null == cacheKey || cacheKey.isEmpty()) {
                continue;
            }
            if (log.isDebugEnabled()) {
                log.debug("delete cache {}", cacheKey);
            }
            key = AbstractRedisCacheOperator.KEY_SERIALIZER.serialize(cacheKey);
            pipeline.del(key);
        }

    }
}
