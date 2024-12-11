package cn.itmtx.ezcache.cacher.redis;

import cn.itmtx.ezcache.bo.CacheKeyBo;
import cn.itmtx.ezcache.bo.CacheWrapper;
import cn.itmtx.ezcache.cacher.ICacheOperator;
import cn.itmtx.ezcache.serializer.ISerializer;
import cn.itmtx.ezcache.serializer.StringSerializer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @Author jc.yin
 * @Date 2024/12/10
 * @Description
 **/
public abstract class AbstractRedisCacheOperator implements ICacheOperator {

    public static final StringSerializer KEY_SERIALIZER = new StringSerializer();

    protected final ISerializer<Object> serializer;

    public AbstractRedisCacheOperator(ISerializer<Object> serializer) {
        this.serializer = serializer;
    }

    protected abstract IRedisClient getRedis();

    /**
     * 写数据到 cache
     *
     * @param cacheKeyBo   key
     * @param cacheWrapper 缓存数据
     * @param method       方法
     */
    @Override
    public void set(CacheKeyBo cacheKeyBo, CacheWrapper cacheWrapper, Method method) {

    }

    /**
     * 根据 key 从 cache 中读取数据
     *
     * @param cacheKeyBo
     * @param method
     * @return
     */
    @Override
    public CacheWrapper<Object> get(CacheKeyBo cacheKeyBo, Method method) {
        return null;
    }

    /**
     * 删除缓存
     *
     * @param cacheKeyBos
     */
    @Override
    public void delete(Set<CacheKeyBo> cacheKeyBos) {

    }

    /**
     * TODO 批量反序列化缓存 value
     * @param keys
     * @param values
     * @param returnType
     * @return
     * @throws Exception
     */
    public Map<CacheKeyBo, CacheWrapper<Object>> deserialize(Set<CacheKeyBo> keys, Collection<Object> values, Type returnType) throws Exception {
        return null;
    }
}
