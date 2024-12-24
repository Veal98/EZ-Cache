package cn.itmtx.ezcache.core.listener;

import cn.itmtx.ezcache.common.bo.CacheKeyBo;
import cn.itmtx.ezcache.common.bo.CacheWrapper;

import java.util.Set;

/**
 * TODO 缓存变更监听器：hot key 检测
 * 参考得物技术: https://blog.csdn.net/SmartCodeTech/article/details/144054333
 */
public interface CacheChangeListener {

    /**
     * 缓存更新
     *
     * @param cacheKeyBo 缓存Key
     * @param newVal   新缓存值
     */
    void update(CacheKeyBo cacheKeyBo, CacheWrapper<Object> newVal);

    /**
     * 缓存删除
     *
     * @param keys 缓存Key
     */
    void delete(Set<CacheKeyBo> keys);

    /**
     * 缓存读取
     * @param cacheKeyBo
     * @param cacheWrapper
     */
    void query(CacheKeyBo cacheKeyBo, CacheWrapper<Object> cacheWrapper);
}
