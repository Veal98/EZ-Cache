package cn.itmtx.ezcache.serializer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 简单的 String 序列化
 */
public class StringSerializer implements ISerializer<String> {

    private final Charset charset;

    public StringSerializer() {
        this(StandardCharsets.UTF_8);
    }

    public StringSerializer(Charset charset) {
        this.charset = charset;
    }

    @Override
    public byte[] serialize(String string) {
        return (string == null ? null : string.getBytes(charset));
    }

    @Override
    public String deserialize(byte[] bytes) throws Exception {
        return (bytes == null ? null : new String(bytes, charset));
    }

}
