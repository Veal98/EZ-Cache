package cn.itmtx.ezcache.starter.admin;

import cn.itmtx.ezcache.common.bo.CacheKeyBo;
import cn.itmtx.ezcache.core.CacheProcessor;
import cn.itmtx.ezcache.core.autorefresh.AutoRefreshBo;
import cn.itmtx.ezcache.core.proxy.ICacheProxy;
import cn.itmtx.ezcache.starter.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author jc.yin
 * @Date 2024/12/10
 * @Description EzCache 管理端
 **/
@RestController
@RequestMapping("/ezcache")
public class EzCacheAdminController {

    private static final Logger log = LoggerFactory.getLogger(EzCacheAdminController.class);

    @Autowired
    private CacheProcessor cacheProcessor;

    /**
     * 获取所有的自动刷新任务
     * @return
     */
    @GetMapping("listAutoRefresh")
    public AutoRefreshVo[] listAutoRefresh() {
        AutoRefreshBo queue[] = cacheProcessor.getAutoRefreshProcessor().getAllAutoRefreshBos();
        if (null == queue || queue.length == 0) {
            return null;
        }
        AutoRefreshVo[] autoRefreshVos = new AutoRefreshVo[queue.length];
        for (int i = 0; i < queue.length; i++) {
            AutoRefreshBo bo = queue[i];
            ICacheProxy proxy = bo.getProxy();
            String className = proxy.getTarget().getClass().getName();
            String methodName = proxy.getMethod().getName();
            CacheKeyBo cacheKeyBo = bo.getCacheKey();

            AutoRefreshVo autoRefreshVo = new AutoRefreshVo();
            autoRefreshVo.setNamespace(cacheKeyBo.getNamespace());
            autoRefreshVo.setKey(cacheKeyBo.getKey());
            autoRefreshVo.setMethod(className + "." + methodName);
            autoRefreshVo.setFirstRequestTimeMillis(DateUtils.formatDate(bo.getFirstRequestTimeMillis()));
            autoRefreshVo.setLastRequestTimeMillis(DateUtils.formatDate(bo.getLastRequestTimeMillis()));
            autoRefreshVo.setRequestCount(bo.getRequestCount());

            autoRefreshVo.setExpireTimeMillis(bo.getCache().expireTimeMillis());
            // 到期时间
            autoRefreshVo.setExpireTimeStr(DateUtils.formatDate(bo.getLastLoadTimeMillis() + bo.getCache().expireTimeMillis()));

            autoRefreshVo.setAutoRefreshNoRequestTimeoutMillis(bo.getCache().autoRefreshNoRequestTimeoutMillis());
            // 停止自动刷新的时间
            autoRefreshVo.setAutoRefreshNoRequestTimeoutStr(DateUtils.formatDate(bo.getLastRequestTimeMillis() + bo.getCache().autoRefreshNoRequestTimeoutMillis()));

            autoRefreshVo.setLastLoadTimeStr(DateUtils.formatDate(bo.getLastLoadTimeMillis()));
            autoRefreshVo.setLoadCount(bo.getLoadCount());
            autoRefreshVo.setAvgLoadDataTimeMillis(bo.getAvgLoadDataTimeMillis());

            autoRefreshVos[i] = autoRefreshVo;
        }
        return autoRefreshVos;
    }

    /**
     * 删除缓存
     * @param key
     * @param hfield
     * @return
     */
    @PostMapping("removeCache")
    public boolean removeCache(String key, String hfield) {
        CacheKeyBo cacheKeyBo = new CacheKeyBo(cacheProcessor.getEzCacheConfig().getNamespace(), key);
        try {
            Set<CacheKeyBo> keys = new HashSet<>();
            keys.add(cacheKeyBo);
            cacheProcessor.deleteCache(keys);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除自动刷新任务
     * @param key
     * @return
     */
    @PostMapping("removeAutoRefresh")
    public boolean removeAutoRefresh(String key) {
        CacheKeyBo cacheKeyBo = new CacheKeyBo(cacheProcessor.getEzCacheConfig().getNamespace(), key);
        try {
            cacheProcessor.getAutoRefreshProcessor().removeAutoRefreshBo(cacheKeyBo);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }
}
