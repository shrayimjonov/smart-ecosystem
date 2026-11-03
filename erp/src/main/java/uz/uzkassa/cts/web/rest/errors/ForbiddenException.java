package uz.uzkassa.cts.web.rest.errors;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends AppGlobalException {

    public ForbiddenException(String title) {
        this(title, title);
    }

    public ForbiddenException(String title, String message) {
        super(title, message, HttpStatus.FORBIDDEN);
    }
}
