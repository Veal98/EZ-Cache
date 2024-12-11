package cn.itmtx.ezcache.serializer;

import java.lang.reflect.Type;

/**
 * @Author jc.yin
 * @Date 2024/12/11
 * @Description
 **/
public class HessianSerializer implements ISerializer<Object>{
    /**
     * Serialize the given object to binary data.
     *
     * @param obj object to serialize
     * @return the equivalent binary data
     * @throws Exception 异常
     */
    @Override
    public byte[] serialize(Object obj) throws Exception {
        return new byte[0];
    }

    /**
     * Deserialize an object from the given binary data.
     *
     * @param bytes      object binary representation
     * @param returnType the GenericReturnType of AOP Method
     * @return the equivalent object instance, 必须是CacheWrapper类型的
     * @throws Exception 异常
     */
    @Override
    public Object deserialize(byte[] bytes, Type returnType) throws Exception {
        return null;
    }
}
