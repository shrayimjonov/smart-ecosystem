package uz.uzkassa.cts.web.rest.errors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@NoArgsConstructor
public class IntegrationException extends RuntimeException {

    ErrorDTO error;
    HttpStatus status;

    public IntegrationException(String message) {
        super(message);
    }

    public IntegrationException(String message, ErrorDTO error) {
        super(message);
        this.error = error;
    }

    public IntegrationException(String message, ErrorDTO error, HttpStatus status) {
        this(message, error);
        this.status = status;
    }

}
