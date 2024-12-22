package cn.itmtx.ezcache.core.autorefresh;

import java.util.Comparator;

/**
 * TODO 自动刷新队列的排序算法：越接近过期时间，越耗时的排在最前
 */
public class AutoRefreshOldestComparator implements Comparator<AutoRefreshBo> {
    @Override
    public int compare(AutoRefreshBo o1, AutoRefreshBo o2) {
        return 0;
    }
}
