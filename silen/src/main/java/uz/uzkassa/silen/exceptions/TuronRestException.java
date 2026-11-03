package uz.uzkassa.silen.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, code = HttpStatus.SERVICE_UNAVAILABLE, reason = "SERVICE_UNAVAILABLE")
public class TuronRestException extends RuntimeException {
    private final String code;
    private final int errorCode;

    public TuronRestException(String code, String message, int errorCode) {
        super(message);
        this.code = code;
        this.errorCode = errorCode;
    }

}
