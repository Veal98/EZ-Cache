package cn.itmtx.ezcache.core.utils;

import cn.itmtx.ezcache.common.annotation.EzCache;

public class RefreshUtils {

    public static final long ALARM_REFRESH_TIME_MILLIS_THRESHOLD = 10 * 60 * 1000;

    public static final int MIN_REFRESH_TIME_MILLIS = 2 * 60 * 1000;

    /**
     * 判断缓存数据是否需要刷新
     * @param ezCache
     * @param expireMillis 缓存过期时间
     * @param lastLoadTimeMillis 该缓存数据上次从 datasource load 的时间
     * @return
     */
    public static ExecuteRefreshBo isAllowRefresh(EzCache ezCache, long expireMillis, long lastLoadTimeMillis) {
        // 如果过期时间太小了，则不进行刷新，避免刷新过于频繁，影响系统稳定性
        if (expireMillis < MIN_REFRESH_TIME_MILLIS) {
            return ExecuteRefreshBo.buildNoRefresh();
        }

        // 计算刷新间隔
        long alarmTimeMillis = ezCache.refreshAlarmTimeMillis();
        long refreshMillis;
        if (alarmTimeMillis > 0 && alarmTimeMillis < expireMillis) {
            refreshMillis = expireMillis - alarmTimeMillis;
        } else {
            // 如果没有设置预警刷新时间，给一个默认时间
            if (alarmTimeMillis >= ALARM_REFRESH_TIME_MILLIS_THRESHOLD) {
                // 缓存过期前 2min 执行刷新
                refreshMillis = expireMillis - MIN_REFRESH_TIME_MILLIS;
            } else {
                // 缓存过期前 1min 执行刷新
                refreshMillis = expireMillis - MIN_REFRESH_TIME_MILLIS / 2;
            }
        }

        // 上次从 datasource 加载数据的时间间隔 < refreshMillis，则不需要进行刷新
        boolean isExecuteRefresh = System.currentTimeMillis() - lastLoadTimeMillis >= refreshMillis;

        return isExecuteRefresh ? ExecuteRefreshBo.buildRefresh(refreshMillis) : ExecuteRefreshBo.buildNoRefresh();
    }

    public static class ExecuteRefreshBo {
        /**
         * 是否允许进行刷新
         */
        Boolean allowRefresh;

        /**
         * 若允许进行刷新，返回计算出来的刷新间隔
         */
        Long refreshMillis;

        public static ExecuteRefreshBo buildRefresh(Long refreshMillis) {
            return new ExecuteRefreshBo(true, refreshMillis);
        }

        public static ExecuteRefreshBo buildNoRefresh() {
            return new ExecuteRefreshBo(false, null);
        }

        public ExecuteRefreshBo(Boolean allowRefresh, Long refreshMillis) {
            this.allowRefresh = allowRefresh;
            this.refreshMillis = refreshMillis;
        }

        public Boolean getAllowRefresh() {
            return allowRefresh;
        }

        public void setAllowRefresh(Boolean allowRefresh) {
            this.allowRefresh = allowRefresh;
        }

        public Long getRefreshMillis() {
            return refreshMillis;
        }

        public void setRefreshMillis(Long refreshMillis) {
            this.refreshMillis = refreshMillis;
        }
    }
}
