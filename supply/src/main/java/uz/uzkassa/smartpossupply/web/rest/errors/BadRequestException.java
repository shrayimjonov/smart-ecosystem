package uz.uzkassa.smartpossupply.web.rest.errors;

import org.springframework.http.HttpStatus;


public class BadRequestException extends AppGlobalException {

    public BadRequestException(String title) {
        this(title, null);
    }

    public BadRequestException(String title, String message) {
        super(title, message, HttpStatus.BAD_REQUEST);
    }
}
