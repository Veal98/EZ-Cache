package cn.itmtx.ezcache.autoconfigure;

import cn.itmtx.ezcache.bo.CacheConfigBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;

/**
 * @Author jc.yin
 * @Date 2024/12/10
 * @Description
 **/
@ConfigurationProperties(prefix = EzCacheProperties.PREFIX)
public class EzCacheProperties {

    public static final String PREFIX = "ezcache";

    private CacheConfigBo cacheConfigBo = new CacheConfigBo();

    @Autowired
    private Environment env;

    /**
     * 是否开启 EzCache 注解
     */
    private boolean enable = true;

    /**
     * 是否使用 namespace
     */
    private boolean namespaceEnable = true;

    /**
     * EzCache 注解 AOP 执行顺序
     */
    private int aopOrder = Integer.MAX_VALUE;

    public CacheConfigBo getCacheConfigBo() {
        return cacheConfigBo;
    }

    public void setCacheConfigBo(CacheConfigBo cacheConfigBo) {
        this.cacheConfigBo = cacheConfigBo;
    }

    public Environment getEnv() {
        return env;
    }

    public void setEnv(Environment env) {
        this.env = env;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isNamespaceEnable() {
        return namespaceEnable;
    }

    public void setNamespaceEnable(boolean namespaceEnable) {
        this.namespaceEnable = namespaceEnable;
    }

    public int getAopOrder() {
        return aopOrder;
    }

    public void setAopOrder(int aopOrder) {
        this.aopOrder = aopOrder;
    }
}
