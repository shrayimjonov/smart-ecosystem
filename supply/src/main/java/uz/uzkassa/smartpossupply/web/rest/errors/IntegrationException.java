package uz.uzkassa.smartpossupply.web.rest.errors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;


@Getter
@Setter
public class IntegrationException extends AppGlobalException {

    String title;

    HttpStatus status;

    Integer customStatus;

    ErrorDTO error;

    public IntegrationException(String title) {
        this(title, null);
    }

    public IntegrationException(String title, String message) {
        this(title, message, HttpStatus.INTERNAL_SERVER_ERROR);
        this.title = title;
    }

    public IntegrationException(String title, String message, HttpStatus status) {
        super(title, message, HttpStatus.valueOf(status.value()));
        this.title = title;
    }

    public IntegrationException(String title, String message, HttpStatusCode status) {
        this(title, message, HttpStatus.valueOf(status.value()));
    }

    public IntegrationException(String title, String message, HttpStatusCode status, ErrorDTO error) {
        this(title, message, HttpStatus.valueOf(status.value()));
        this.error = error;
    }
}
