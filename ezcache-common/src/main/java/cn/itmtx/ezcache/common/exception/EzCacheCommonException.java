package cn.itmtx.ezcache.common.exception;

import cn.itmtx.ezcache.common.enums.ErrorCodeEnum;

/**
 * @Author jc.yin
 * @Date 2024/12/19
 * @Description 通用异常（非业务异常）
 **/
public class EzCacheCommonException extends Exception{

    private String errorCode;

    private String errorMessage;

    public EzCacheCommonException(ErrorCodeEnum errorCodeEnum) {
        this.errorCode = errorCodeEnum.getErrorCode();
        this.errorMessage = errorCodeEnum.getDefaultErrorMessage();
    }

    public EzCacheCommonException(ErrorCodeEnum errorCodeEnum, Throwable cause) {
        super(cause);
        this.errorCode = errorCodeEnum.getErrorCode();
        this.errorMessage = errorCodeEnum.getDefaultErrorMessage();
    }

    public EzCacheCommonException(ErrorCodeEnum errorCodeEnum, String errorMessage, Throwable cause) {
        super(cause);
        this.errorCode = errorCodeEnum.getErrorCode();
        this.errorMessage = errorMessage;
    }

    /**
     * Constructs a new runtime exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A <tt>null</tt> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public EzCacheCommonException(String errorCode, String errorMessage, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public EzCacheCommonException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
