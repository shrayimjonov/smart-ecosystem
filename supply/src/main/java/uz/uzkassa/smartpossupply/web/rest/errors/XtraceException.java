package uz.uzkassa.smartpossupply.web.rest.errors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter
public class XtraceException extends RuntimeException {

    String title;

    HttpStatus status;

    Integer customStatus;

    String entityId;

    public XtraceException(String message) {
        super(message);
    }

    public XtraceException(String title, String message) {
        this(title, message, null);
    }

    public XtraceException(String title, String message, HttpStatus status) {
        this(title, message, status, null);
    }

    public XtraceException(String title, String message, HttpStatus status, Integer customStatus) {
        this(title, message, status, customStatus, null);
    }

    public XtraceException(String title, String message, HttpStatus status, Integer customStatus, String entityId) {
        super(message);
        this.title = title;
        this.status = status;
        this.customStatus = customStatus;
        this.entityId = entityId;
    }
}
