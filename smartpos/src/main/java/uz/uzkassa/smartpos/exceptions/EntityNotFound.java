package uz.uzkassa.smartpos.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Entity not found")
public class EntityNotFound extends RuntimeException {
    private Integer errorCode = -1;

    public EntityNotFound(String message) {
        super(message);
    }

    public EntityNotFound(String message, Object id) {
        this(message + ", ID: " + id);
    }

    public EntityNotFound(String message, Integer errorCode) {
        this(message);
        this.errorCode = errorCode;
    }
}
