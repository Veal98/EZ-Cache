package cn.itmtx.ezcache.operator.map;

import cn.itmtx.ezcache.common.bo.CacheKeyBo;
import cn.itmtx.ezcache.common.bo.CacheWrapper;
import cn.itmtx.ezcache.common.bo.EzCacheConfig;
import cn.itmtx.ezcache.operator.ICacheOperator;
import cn.itmtx.ezcache.operator.bo.CacheBatchSetBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于 ConcurrentHashMap 实现缓存
 */
public class ConcurrentHashMapCacheOperator implements ICacheOperator {

    private static final Logger logger = LoggerFactory.getLogger(ConcurrentHashMapCacheOperator.class);

    /**
     * 缓存区域
     */
    private final ConcurrentHashMap<String, Object> map;

    /**
     * map 初始化大小
     */
    private static int MAP_INIT_SIZE = 1024;

    private final EzCacheConfig ezCacheConfig;

    /**
     * map 变更通知
     */
    private final MapChangeTask mapChangeTask;

    private final Thread mapChangeThread;

    public ConcurrentHashMapCacheOperator(EzCacheConfig ezCacheConfig) {
        this.ezCacheConfig = ezCacheConfig;
        this.map = new ConcurrentHashMap<>(MAP_INIT_SIZE);

        this.mapChangeTask = new MapChangeTask(this, ezCacheConfig);
        this.mapChangeThread = new Thread(mapChangeTask);
        mapChangeTask.start();
        mapChangeThread.start();
    }

    @Override
    public void shutdown() {
        try {
            mapChangeTask.destroy();
            if (mapChangeThread != null) {
                mapChangeThread.interrupt();
            }
            logger.info("ConcurrentHashMapCacheOperator shutdown.");
        } catch (Exception e) {
            logger.error("ConcurrentHashMapCacheOperator shutdown failed.", e);
        }
    }

    @ Override
    public void setCache(CacheKeyBo cacheKeyBo, CacheWrapper<Object> cacheWrapper) {
        if (null == cacheKeyBo) {
            return ;
        }
        if (null == cacheWrapper || cacheWrapper.getExpireMillis() < 0) {
            return ;
        }

        String cacheKey = cacheKeyBo.getCacheKey();
        if (null == cacheKey || cacheKey.isEmpty()) {
            return;
        }
        // 软引用
        SoftReference<CacheWrapper<Object>> reference = new SoftReference<CacheWrapper<Object>>(cacheWrapper);
        // 放入 map
        map.put(cacheKey, reference);
        // 通知 map 变更
        mapChangeTask.mapChangeSingle();
    }

    @Override
    public void batchSetCache(Set<CacheBatchSetBo> cacheBatchSetBos) {
        if (null == cacheBatchSetBos || cacheBatchSetBos.isEmpty()) {
            return;
        }
        for (CacheBatchSetBo setBo : cacheBatchSetBos) {
            if (null == setBo) {
                continue;
            }
            this.setCache(setBo.getCacheKeyBo(), setBo.getResult());
        }
    }

    @Override
    public CacheWrapper<Object> getCache(CacheKeyBo cacheKeyBo) {
        if (null == cacheKeyBo) {
            return null;
        }

        String cacheKey = cacheKeyBo.getCacheKey();
        if (null == cacheKey || cacheKey.isEmpty()) {
            return null;
        }

        Object mapValue = map.get(cacheKey);
        if (null == mapValue) {
            return null;
        }
        // map 中存的是软引用，从软引用中拿出真正的值
        CacheWrapper<Object> result = null;
        if (mapValue instanceof SoftReference) {
            SoftReference<CacheWrapper<Object>> reference = (SoftReference<CacheWrapper<Object>>) mapValue;
            result = reference.get();
        }

        if (null == result || result.isExpired()) {
            return null;
        }

        return result;
    }

    @Override
    public Map<CacheKeyBo, CacheWrapper<Object>> batchGetCache(Set<CacheKeyBo> keys) throws Exception {
        if (null == keys || keys.isEmpty()) {
            return Collections.emptyMap();
        }
        int len = keys.size();
        Map<CacheKeyBo, CacheWrapper<Object>> res = new HashMap<>(len);
        for (CacheKeyBo key : keys) {
            CacheWrapper<Object> value = this.getCache(key);
            if (null != value) {
                res.put(key, value);
            }
        }
        return res;
    }

    @Override
    public void deleteCache(Set<CacheKeyBo> cacheKeyBos) {
        if (null == cacheKeyBos || cacheKeyBos.isEmpty()) {
            return ;
        }

        for (CacheKeyBo cacheKeyBo : cacheKeyBos) {
            if (null == cacheKeyBo) {
                continue;
            }
            String cacheKey = cacheKeyBo.getCacheKey();
            if (null == cacheKey || cacheKey.isEmpty()) {
                continue;
            }

            Object mapValue = map.remove(cacheKey);
            if (null != mapValue) {
                this.mapChangeTask.mapChangeSingle();
            }
        }
    }

    public EzCacheConfig getEzCacheConfigBo() {
        return ezCacheConfig;
    }

    public ConcurrentHashMap<String, Object> getMap() {
        return map;
    }
}
