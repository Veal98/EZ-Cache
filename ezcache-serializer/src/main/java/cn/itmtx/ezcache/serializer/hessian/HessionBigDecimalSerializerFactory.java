package cn.itmtx.ezcache.serializer.hessian;

import com.caucho.hessian.io.*;

import java.math.BigDecimal;

/**
 * 自定义 BigDecimal SerializerFactory
 */
public class HessionBigDecimalSerializerFactory extends AbstractSerializerFactory {

    private static final StringValueSerializer BIG_DECIMAL_SERIALIZER = new StringValueSerializer();

    private static final BigDecimalDeserializer BIG_DECIMAL_DESERIALIZER = new BigDecimalDeserializer();

    @Override
    public Serializer getSerializer(Class cl) throws HessianProtocolException {
        if (BigDecimal.class.isAssignableFrom(cl)) {
            return BIG_DECIMAL_SERIALIZER;
        }
        return null;
    }

    @Override
    public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
        if (BigDecimal.class.isAssignableFrom(cl)) {
            return BIG_DECIMAL_DESERIALIZER;
        }
        return null;
    }

}
