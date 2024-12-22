package cn.itmtx.ezcache.core.autorefresh;

import java.util.Comparator;

/**
 * TODO 自动刷新队列的排序算法：根据请求次数倒序排序，请求次数越多，说明使用频率越高
 */
public class AutoRefreshRequestCountComparator implements Comparator<AutoRefreshBo> {
    @Override
    public int compare(AutoRefreshBo o1, AutoRefreshBo o2) {
        return 0;
    }
}
