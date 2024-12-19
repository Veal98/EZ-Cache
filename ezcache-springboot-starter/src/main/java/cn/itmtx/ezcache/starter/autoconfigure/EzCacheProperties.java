package cn.itmtx.ezcache.starter.autoconfigure;

import cn.itmtx.ezcache.common.bo.EzCacheConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

/**
 * @Author jc.yin
 * @Date 2024/12/10
 * @Description
 **/
@ConfigurationProperties(prefix = EzCacheProperties.PREFIX)
public class EzCacheProperties {

    public static final String PREFIX = "ezcache";

    private EzCacheConfig config = new EzCacheConfig();

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

    /**
     * EzCacheDelete 注解 AOP 执行顺序
     */
    private int deleteAopOrder = Integer.MAX_VALUE;

    /**
     * 是否使用 Cglib 创建代理; 若为 false 则使用标准的 JDK 动态代理
     */
    private boolean cglibProxyTargetClass = true;

    @PostConstruct
    public void init() {
        if (namespaceEnable && null != env) {
            String namespace = config.getNamespace();
            // 如果没有指定 namespace 则使用 applicationName 作为 namespace
            if (null == namespace || namespace.trim().length() == 0) {
                String applicationName = env.getProperty("spring.application.name");
                if (null != applicationName && applicationName.trim().length() > 0) {
                    config.setNamespace(applicationName);
                }
            }
        }

    }

    public EzCacheConfig getCacheConfigBo() {
        return config;
    }

    public void setCacheConfigBo(EzCacheConfig ezCacheConfig) {
        this.config = ezCacheConfig;
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

    public int getDeleteAopOrder() {
        return deleteAopOrder;
    }

    public void setDeleteAopOrder(int deleteAopOrder) {
        this.deleteAopOrder = deleteAopOrder;
    }

    public boolean isCglibProxyTargetClass() {
        return cglibProxyTargetClass;
    }

    public void setCglibProxyTargetClass(boolean cglibProxyTargetClass) {
        this.cglibProxyTargetClass = cglibProxyTargetClass;
    }
}
