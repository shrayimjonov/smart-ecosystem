package uz.uzkassa.silen.exceptions;

public class RestException extends RuntimeException {
    private String code;
    private int errorCode;

    public RestException(String code, String message, int errorCode) {
        super(message);
        this.code = code;
        this.errorCode = errorCode;
    }

    public String getCode() {
        return code;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
