package uz.uzkassa.horeca.web.rest.errors;

import org.springframework.http.HttpStatus;


public class ConflictException extends AppGlobalException {

    public ConflictException(String title) {
        this(title, title);
    }

    public ConflictException(String title, String message) {
        super(title, message, HttpStatus.CONFLICT);
    }
}
