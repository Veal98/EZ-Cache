package cn.itmtx.ezcache.core.autorefresh.comparator;

import cn.itmtx.ezcache.core.autorefresh.AutoRefreshBo;

import java.util.Comparator;

/**
 * 自动刷新队列的排序算法：根据请求次数倒序排序，请求次数越多，说明使用频率越高
 */
public class AutoRefreshMostTimesComparator implements Comparator<AutoRefreshBo> {
    @Override
    public int compare(AutoRefreshBo o1, AutoRefreshBo o2) {
        if (o1 == null) {
            return ComparatorSwapEnum.SWAP.getResult();
        }
        if (o2 == null) {
            return ComparatorSwapEnum.NOT_SWAP.getResult();
        }

        return o1.getRequestCount() < o2.getRequestCount() ? ComparatorSwapEnum.SWAP.getResult() : ComparatorSwapEnum.NOT_SWAP.getResult();
    }
}
