package uz.uzkassa.horeca.web.rest.errors;

import org.springframework.http.HttpStatus;


public class UnauthorizedException extends AppGlobalException {

    public UnauthorizedException(String title) {
        this(title, null);
    }

    public UnauthorizedException(String title, String message) {
        super(title, message, HttpStatus.UNAUTHORIZED);
    }
}
