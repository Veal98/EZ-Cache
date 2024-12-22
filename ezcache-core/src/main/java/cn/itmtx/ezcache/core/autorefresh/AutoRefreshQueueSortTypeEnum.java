package cn.itmtx.ezcache.core.autorefresh;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * 自动刷新队列排序方法
 */
public enum AutoRefreshQueueSortTypeEnum {

    NONE(0, null),

    /**
     * 越接近过期时间，越耗时的排在最前
     */
    OLDEST_FIRST(1, new AutoRefreshOldestComparator()),

    /**
     * 根据请求次数，倒序排序，请求次数越多，说明使用频率越高，造成并发的可能越大。
     */
    REQUEST_TIMES_DESC(2, new AutoRefreshRequestCountComparator());


    private Integer sortId;

    private Comparator<AutoRefreshBo> comparator;

    AutoRefreshQueueSortTypeEnum(Integer sortId, Comparator<AutoRefreshBo> comparator) {
        this.sortId = sortId;
        this.comparator = comparator;
    }

    public static AutoRefreshQueueSortTypeEnum findBy(Integer sortId) {
        if (Objects.isNull(sortId)) {
            return NONE;
        }

        return Arrays.stream(AutoRefreshQueueSortTypeEnum.values())
                .filter(item -> item.getSortId().equals(sortId))
                .findFirst()
                .orElse(NONE);
    }

    public Integer getSortId() {
        return sortId;
    }

    public Comparator<AutoRefreshBo> getComparator() {
        return comparator;
    }
}
