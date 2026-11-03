package uz.uzkassa.horeca.web.rest.errors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;


@Getter
@Setter
public class FacturaException extends RuntimeException {

    String title;

    HttpStatus status;

    Integer customStatus;

    String entityId;

    public FacturaException(String message) {
        super(message);
    }

    public FacturaException(String title, String message) {
        this(title, message, null);
    }

    public FacturaException(String title, String message, HttpStatus status) {
        this(title, message, status, null);
    }

    public FacturaException(String title, String message, HttpStatusCode statusCode) {
        this(title, message, HttpStatus.valueOf(statusCode.value()), null);
    }

    public FacturaException(String title, String message, HttpStatus status, Integer customStatus) {
        this(title, message, status, customStatus, null);
    }

    public FacturaException(String title, String message, HttpStatus status, Integer customStatus, String entityId) {
        super(message);
        this.title = title;
        this.status = status;
        this.customStatus = customStatus;
        this.entityId = entityId;
    }
}
