package uz.uzkassa.smartpossupply.web.rest.errors;

import org.springframework.http.HttpStatus;


public class AlreadyExistException extends AppGlobalException {

    public AlreadyExistException(String title) {
        this(title, null);
    }

    public AlreadyExistException(String title, String message) {
        super(title, message, HttpStatus.NOT_FOUND);
    }
}
