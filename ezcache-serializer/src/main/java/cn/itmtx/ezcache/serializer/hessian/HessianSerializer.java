package cn.itmtx.ezcache.serializer.hessian;

import cn.itmtx.ezcache.serializer.ISerializer;
import com.caucho.hessian.io.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @Description Hessian 序列化
 **/
public class HessianSerializer implements ISerializer<Object> {

    private static final SerializerFactory SERIALIZER_FACTORY = new SerializerFactory();

    static {
        // 支持 BigDecimal 序列化
        SERIALIZER_FACTORY.addFactory(new HessionBigDecimalSerializerFactory());
        // 支持 SoftReference 序列化
        SERIALIZER_FACTORY.addFactory(new HessionSoftReferenceSerializerFactory());
    }

    /**
     * Serialize the given object to binary data.
     *
     * @param obj object to serialize
     * @return the equivalent binary data
     * @throws Exception 异常
     */
    @Override
    public byte[] serialize(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        AbstractHessianOutput output = new Hessian2Output(outputStream);
        output.setSerializerFactory(SERIALIZER_FACTORY);
        // 将对象写到流里
        output.writeObject(obj);
        output.flush();
        byte[] val = outputStream.toByteArray();
        output.close();
        return val;
    }

    /**
     * Deserialize an object from the given binary data.
     *
     * @param bytes      object binary representation
     * @return the equivalent object instance, 必须是CacheWrapper类型的
     * @throws Exception 异常
     */
    @Override
    public Object deserialize(byte[] bytes) throws Exception {
        if (null == bytes || bytes.length == 0) {
            return null;
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        AbstractHessianInput input = new Hessian2Input(inputStream);
        input.setSerializerFactory(SERIALIZER_FACTORY);
        Object obj = input.readObject();
        input.close();
        return obj;
    }
}
