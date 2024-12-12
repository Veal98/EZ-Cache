package cn.itmtx.ezcache.bo;

import java.util.Arrays;
import java.util.Objects;

/**
 * @Description 批量 set cache(key/value 已序列化)
 **/
public class CacheBatchByteSetBo {

    private byte[] keyByte;

    private byte[] resultByte;

    public byte[] getKeyByte() {
        return keyByte;
    }

    public void setKeyByte(byte[] keyByte) {
        this.keyByte = keyByte;
    }

    public byte[] getResultByte() {
        return resultByte;
    }

    public void setResultByte(byte[] resultByte) {
        this.resultByte = resultByte;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CacheBatchByteSetBo byteSetBo = (CacheBatchByteSetBo) o;
        return Objects.deepEquals(keyByte, byteSetBo.keyByte) && Objects.deepEquals(resultByte, byteSetBo.resultByte);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(keyByte), Arrays.hashCode(resultByte));
    }

    @Override
    public String toString() {
        return "CacheBatchByteSetBo{" +
                "keyByte=" + Arrays.toString(keyByte) +
                ", resultByte=" + Arrays.toString(resultByte) +
                '}';
    }
}
