package cn.itmtx.ezcache.operator.redis;

import cn.itmtx.ezcache.common.bo.CacheKeyBo;
import cn.itmtx.ezcache.common.bo.CacheWrapper;
import cn.itmtx.ezcache.operator.ICacheOperator;
import cn.itmtx.ezcache.operator.bo.CacheBatchByteSetBo;
import cn.itmtx.ezcache.operator.bo.CacheBatchSetBo;
import cn.itmtx.ezcache.serializer.ISerializer;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author jc.yin
 * @Date 2024/12/10
 * @Description 基于 Redis 实现缓存
 **/
public abstract class AbstractRedisCacheOperator implements ICacheOperator {

    private static final Logger log = LoggerFactory.getLogger(AbstractRedisCacheOperator.class);

    /**
     * 序列化器
     */
    private final ISerializer<Object> serializer;

    public AbstractRedisCacheOperator(ISerializer<Object> serializer) {
        this.serializer = serializer;
    }

    /**
     * 直接操作 redis 的 client
     *
     * @return
     */
    protected abstract IRedisClient getRedisClient();

    /**
     * 写数据到 cache
     *
     * @param cacheKeyBo   key
     * @param cacheWrapper 缓存数据
     */
    @Override
    public void setCache(CacheKeyBo cacheKeyBo, CacheWrapper<Object> cacheWrapper) {
        if (null == cacheKeyBo) {
            return;
        }
        String cacheKey = cacheKeyBo.getCacheKey();
        if (null == cacheKey || cacheKey.isEmpty()) {
            return;
        }
        try (IRedisClient redisClient = this.getRedisClient()) {
            byte[] key = this.getSerializer().serialize(cacheKey);
            byte[] val = this.getSerializer().serialize(cacheWrapper);
            long expireMillis = cacheWrapper.getExpireMillis();
            redisClient.setex(key, expireMillis, val);
        } catch (Exception ex) {
            log.error("setCache error", ex);
        }
    }

    /**
     * 批量写数据
     *
     * @param cacheBatchSetBos
     */
    @Override
    public void batchSetCache(Set<CacheBatchSetBo> cacheBatchSetBos) {
        if (null == cacheBatchSetBos || cacheBatchSetBos.isEmpty()) {
            return;
        }
        try (IRedisClient redisClient = this.getRedisClient()) {
            redisClient.mset(this.serializeBatchSetBo(cacheBatchSetBos));
        } catch (Exception ex) {
            log.error("batchSetCache error", ex);
        }
    }

    private Set<CacheBatchByteSetBo> serializeBatchSetBo(Set<CacheBatchSetBo> cacheBatchSetBos) throws Exception {
        if (null == cacheBatchSetBos || cacheBatchSetBos.isEmpty()) {
            return new HashSet<>();
        }

        Set<CacheBatchByteSetBo> set = new HashSet<>();
        for (CacheBatchSetBo setBo : cacheBatchSetBos) {
            CacheBatchByteSetBo byteSetBo = new CacheBatchByteSetBo();
            String cacheKey = setBo.getCacheKeyBo().getCacheKey();
            byteSetBo.setKeyByte(this.getSerializer().serialize(cacheKey));
            byteSetBo.setResultByte(this.getSerializer().serialize(setBo.getResult()));
            byteSetBo.setExpireMillis(setBo.getResult().getExpireMillis());
            set.add(byteSetBo);
        }

        return set;
    }

    /**
     * 根据 key 从 cache 中读取数据
     *
     * @param cacheKeyBo
     * @return
     */
    @Override
    public CacheWrapper<Object> getCache(CacheKeyBo cacheKeyBo) {
        if (null == cacheKeyBo) {
            return null;
        }

        String cacheKey = cacheKeyBo.getCacheKey();
        if (null == cacheKey || cacheKey.isEmpty()) {
            return null;
        }
        try (IRedisClient redisClient = getRedisClient()) {
            byte[] val = redisClient.get(this.getSerializer().serialize(cacheKey));
            return (CacheWrapper<Object>) this.getSerializer().deserialize(val);
        } catch (Exception ex) {
            log.error("getCache error", ex);
            return null;
        }
    }

    /**
     * 批量读数据
     *
     * @param keys 缓存keys
     * @return 返回已命中的缓存数据(要过滤未命中数据)
     * @throws Exception
     */
    @Override
    public Map<CacheKeyBo, CacheWrapper<Object>> batchGetCache(Set<CacheKeyBo> keys) throws Exception {
        if (null == keys || keys.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<CacheKeyBo, CacheWrapper<Object>> res = new HashMap<>();


        // key: cacheKeyBo 序列化后的 byte 数组, value: CacheKeybo
        Map<byte[], CacheKeyBo> cacheKeyBoMap = new HashMap<>();
        Set<byte[]> keyBytes = new HashSet<>();
        try (IRedisClient redisClient = this.getRedisClient()) {
            for (CacheKeyBo cacheKeyBo : keys) {
                if (null == cacheKeyBo) {
                    continue;
                }
                String cacheKey = cacheKeyBo.getCacheKey();
                if (null != cacheKey && cacheKey.isEmpty()) {
                    byte[] cacheKeyByte = this.getSerializer().serialize(cacheKey);
                    keyBytes.add(cacheKeyByte);
                    cacheKeyBoMap.putIfAbsent(cacheKeyByte, cacheKeyBo);
                }
            }

            // 操作 redis 获取数据
            Map<byte[], byte[]> redisClientResult = redisClient.mget(keyBytes);

            // 处理数据
            for (byte[] keyByte : redisClientResult.keySet()) {
                CacheKeyBo cacheKeyBo = cacheKeyBoMap.get(keyByte);
                if (null == cacheKeyBo) {
                    continue;
                }
                byte[] val = redisClientResult.get(keyByte);
                if (null == val) {
                    continue;
                }
                // 反序列化 value
                CacheWrapper<Object> cacheWrapper = (CacheWrapper<Object>) this.getSerializer().deserialize(val);
                if (null == cacheWrapper) {
                    continue;
                }

                res.putIfAbsent(cacheKeyBo, cacheWrapper);
            }
            return res;
        } catch (Exception ex) {
            log.error("batchGetCache error", ex);
            return res;
        }
    }

    /**
     * 删除缓存
     *
     * @param cacheKeyBos
     */
    @Override
    public void deleteCache(Set<CacheKeyBo> cacheKeyBos) {
        if (null == cacheKeyBos || cacheKeyBos.isEmpty()) {
            return ;
        }

        try(IRedisClient redisClient = getRedisClient()) {
            Set<byte[]> keyBytes = cacheKeyBos.stream()
                    .filter(Objects::nonNull)
                    .map(cacheKeyBo -> {
                        try {
                            return this.getSerializer().serialize(cacheKeyBo.getCacheKey());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toSet());
            redisClient.delete(keyBytes);
        } catch (Exception e) {
            log.error("deleteCache error", e);
        }

    }


    @Override
    public void shutdown() {

    }

    public ISerializer<Object> getSerializer() {
        return serializer;
    }

}
