package cn.itmtx.ezcache.core.autorefresh.comparator;

public enum ComparatorSwapEnum {
    NOT_SWAP(-1),

    SWAP(1);

    private Integer result;

    public Integer getResult() {
        return result;
    }

    ComparatorSwapEnum(Integer result) {
        this.result = result;
    }
}
