package uz.uzkassa.smartpos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AccessDeniedExp extends RuntimeException {
    public AccessDeniedExp(String message) {
        super(message);
    }

}
