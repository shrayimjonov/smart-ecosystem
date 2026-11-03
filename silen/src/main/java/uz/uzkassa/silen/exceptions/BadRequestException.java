package uz.uzkassa.silen.exceptions;

import lombok.Getter;

import java.util.Map;

@Getter
public class BadRequestException extends RestException {
    private Object data;
    private Map<String,Object> errors;

    public BadRequestException() {
        this("Возникла ошибка");
    }

    public BadRequestException(String message) {
        this(message, ErrorConstants.DEFAULT_ERROR_CODE);
    }

    public BadRequestException(String message, Object data) {
        super(ErrorConstants.ERR_VALIDATION, message, ErrorConstants.DEFAULT_ERROR_CODE);
        this.data = data;
    }

    public BadRequestException(String message, Map<String, Object> errors) {
        super(ErrorConstants.ERR_VALIDATION, message, ErrorConstants.DEFAULT_ERROR_CODE);
        this.errors = errors;
    }

    public BadRequestException(String message, int errorCode) {
        super(ErrorConstants.ERR_VALIDATION, message, errorCode);
    }

    public BadRequestException(String message, int errorCode, Object data) {
        super(ErrorConstants.ERR_VALIDATION, message, errorCode);
        this.data = data;
    }

    public BadRequestException(String code, String message, int errorCode, Object data) {
        super(code, message, errorCode);
        this.data = data;
    }
}
