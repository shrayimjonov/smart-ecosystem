package uz.uzkassa.smartpos.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad request")
public class BadRequestException extends RuntimeException {
    private int errorCode = -1;
    private String solution;

    public BadRequestException() {
        this("Произошла ошибка");
    }

    public BadRequestException(String message, int errorCode, String solution) {
        this(message);
        this.errorCode = errorCode;
        this.solution = solution;
    }

    public BadRequestException(String message, int errorCode) {
        this(message);
        this.errorCode = errorCode;
    }

    public BadRequestException(String message) {
        super(message);
    }
}
