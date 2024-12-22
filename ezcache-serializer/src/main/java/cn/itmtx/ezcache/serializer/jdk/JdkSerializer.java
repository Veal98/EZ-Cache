package cn.itmtx.ezcache.serializer.jdk;

import cn.itmtx.ezcache.serializer.ISerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * JDK 序列化
 */
public class JdkSerializer implements ISerializer<Object> {

    @Override
    public byte[] serialize(Object obj) throws Exception {
        if (obj == null) {
            return new byte[0];
        }
        // 将对象写到流里
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(outputStream);
        output.writeObject(obj);
        output.flush();
        return outputStream.toByteArray();
    }

    @Override
    public Object deserialize(byte[] bytes) throws Exception {
        if (null == bytes || bytes.length == 0) {
            return null;
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream input = new ObjectInputStream(inputStream);
        return input.readObject();
    }
}
