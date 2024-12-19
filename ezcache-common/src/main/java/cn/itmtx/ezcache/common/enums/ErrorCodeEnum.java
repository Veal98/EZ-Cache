package cn.itmtx.ezcache.common.enums;

/**
 * @Author jc.yin
 * @Date 2024/12/19
 * @Description
 **/
public enum ErrorCodeEnum {

    KEY_EXPRESSION_PARSE_ERROR("10001", "parse cache key failed or key is empty. please check you cache key."),

    ;

    ErrorCodeEnum(String errorCode, String defaultErrorMessage) {
        this.errorCode = errorCode;
        this.defaultErrorMessage = defaultErrorMessage;
    }

    private String errorCode;

    private String defaultErrorMessage;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getDefaultErrorMessage() {
        return defaultErrorMessage;
    }

    public void setDefaultErrorMessage(String defaultErrorMessage) {
        this.defaultErrorMessage = defaultErrorMessage;
    }
}
