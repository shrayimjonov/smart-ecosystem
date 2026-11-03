package uz.uzkassa.horeca.web.rest.errors;

import org.springframework.http.HttpStatus;


public class NotFoundException extends AppGlobalException {

    public NotFoundException(String title) {
        this(title, null);
    }

    public NotFoundException(String title, String message) {
        super(title, message, HttpStatus.NOT_FOUND);
    }
}
