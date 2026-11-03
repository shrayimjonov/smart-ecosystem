package uz.uzkassa.silen.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Entity not found")
public class EntityNotFoundException extends RestException {
    public EntityNotFoundException(String message) {
        this(message, ErrorConstants.DEFAULT_ERROR_CODE);
    }

    public EntityNotFoundException(String message, int errorCode) {
        super(ErrorConstants.ERR_VALIDATION, message, errorCode);
    }
}
