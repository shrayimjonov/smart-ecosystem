package uz.uzkassa.horeca.web.rest.errors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;


@Getter
@Setter
public class IntegrationException extends AppGlobalException {

    String title;

    HttpStatusCode statusCode;

    Integer customStatus;

    public IntegrationException(String title) {
        this(title, null);
    }

    public IntegrationException(String title, String message) {
        this(title, message, HttpStatus.INTERNAL_SERVER_ERROR);
        this.title = title;
    }

    public IntegrationException(String title, String message, HttpStatusCode status) {
        super(title, message, HttpStatus.valueOf(status.value()));
    }
}
