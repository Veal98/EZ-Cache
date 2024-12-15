package cn.itmtx.ezcache.serializer.hessian;

import com.caucho.hessian.io.*;

import java.io.IOException;
import java.lang.ref.SoftReference;

/**
 * 自定义 SoftReference SerializerFactory
 */
public class HessionSoftReferenceSerializerFactory extends AbstractSerializerFactory {

    private final SoftReferenceSerializer beanSerializer = new SoftReferenceSerializer();

    private final SoftReferenceDeserializer beanDeserializer = new SoftReferenceDeserializer();

    @Override
    public Serializer getSerializer(Class cl) throws HessianProtocolException {
        if (SoftReference.class.isAssignableFrom(cl)) {
            return beanSerializer;
        }
        return null;
    }

    @Override
    public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
        if (SoftReference.class.isAssignableFrom(cl)) {
            return beanDeserializer;
        }
        return null;
    }

    static class SoftReferenceSerializer extends AbstractSerializer implements ObjectSerializer {

        @Override
        public Serializer getObjectSerializer() {
            return this;
        }

        @Override
        public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
            if (out.addRef(obj)) {
                return;
            }
            @SuppressWarnings("unchecked")
            SoftReference<Object> data = (SoftReference<Object>) obj;

            int refV = out.writeObjectBegin(SoftReference.class.getName());

            if (refV == -1) {
                out.writeInt(1);
                out.writeString("ref");
                out.writeObjectBegin(SoftReference.class.getName());
            }
            if (data != null) {
                Object ref = data.get();
                if (null != ref) {
                    out.writeObject(ref);
                } else {
                    out.writeNull();
                }
            } else {
                out.writeNull();
            }
        }
    }

    static class SoftReferenceDeserializer extends AbstractMapDeserializer {

        @Override
        public Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
            try {
                SoftReference<Object> obj = instantiate();
                in.addRef(obj);
                Object value = in.readObject();
                obj = null;
                return new SoftReference<Object>(value);
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw new IOExceptionWrapper(e);
            }

        }

        protected SoftReference<Object> instantiate() throws Exception {
            Object obj = new Object();
            return new SoftReference<Object>(obj);
        }

    }

}
