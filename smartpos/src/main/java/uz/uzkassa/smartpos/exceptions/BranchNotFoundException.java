package uz.uzkassa.smartpos.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Branch not found")
public class BranchNotFoundException extends RuntimeException {
    private int errorCode = -1;

    public BranchNotFoundException(String message, int errorCode) {
        this(message);
        this.errorCode = errorCode;
    }

    public BranchNotFoundException(String message) {
        super(message);
    }
}
