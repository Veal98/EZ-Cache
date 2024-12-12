package cn.itmtx.ezcache.serializer;

import java.lang.reflect.Type;

/**
 * @Author jc.yin
 * @Date 2024/12/10
 * @Description 序列化
 **/
public interface ISerializer<T> {

    /**
     * Serialize the given object to binary data.
     *
     * @param obj object to serialize
     * @return the equivalent binary data
     * @throws Exception 异常
     */
    byte[] serialize(final T obj) throws Exception;

    /**
     * Deserialize an object from the given binary data.
     *
     * @param bytes      object binary representation
     * @return the equivalent object instance, 必须是 CacheWrapper 类型的
     * @throws Exception 异常
     */
    T deserialize(final byte[] bytes) throws Exception;
}
