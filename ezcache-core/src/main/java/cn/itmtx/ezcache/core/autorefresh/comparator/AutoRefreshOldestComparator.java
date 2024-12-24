package cn.itmtx.ezcache.core.autorefresh.comparator;

import cn.itmtx.ezcache.core.autorefresh.AutoRefreshBo;

import java.util.Comparator;

/**
 * 自动刷新队列的排序算法：越接近过期时间越排在前面。若距离过期时长一致，则调用越耗时的越排在前面
 */
public class AutoRefreshOldestComparator implements Comparator<AutoRefreshBo> {
    @Override
    public int compare(AutoRefreshBo o1, AutoRefreshBo o2) {
        if (o1 == null) {
            return ComparatorSwapEnum.SWAP.getResult();
        }
        if (o2 == null) {
            return ComparatorSwapEnum.NOT_SWAP.getResult();
        }

        long now = System.currentTimeMillis();
        // （now - 最近加载数据的时间）- 过期时长，这个值是负数，值越大，说明离过期时间越近
        // ex: now(11:00) - 上次加载时间(10:30) - 过期时长(1h) -> dif = -30min;
        //     now(11:00) - 上次加载时间(10:40) - 过期时长(1h) -> dif = -40min;
        long dif1 = o1.getCache().expireTimeMillis() - (now - o1.getLastLoadTimeMillis());
        long dif2 = now - o2.getLastLoadTimeMillis() - o2.getCache().expireTimeMillis();
        if (dif1 == dif2) {
            return o1.getAvgLoadDataTimeMillis() < o2.getAvgLoadDataTimeMillis() ? ComparatorSwapEnum.SWAP.getResult() : ComparatorSwapEnum.NOT_SWAP.getResult();
        }
        return dif1 < dif2 ? ComparatorSwapEnum.SWAP.getResult() : ComparatorSwapEnum.NOT_SWAP.getResult();
    }
}
