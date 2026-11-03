package uz.uzkassa.smartpos.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User not found")
public class UserNotFoundException extends RuntimeException {
    private Integer errorCode = -1;

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Object id) {
        this(message + ", ID: " + id);
    }

    public UserNotFoundException(String message, Integer errorCode) {
        this(message);
        this.errorCode = errorCode;
    }
}
