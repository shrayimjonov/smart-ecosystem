package uz.uzkassa.horeca.web.rest.errors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.util.Optional;


@Getter
@Setter
public class AppGlobalException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 843954932849L;

    protected String title;
    protected HttpStatus status;

    public AppGlobalException(String title, String message, HttpStatus status) {
        super(Optional.ofNullable(message).orElse(title));
        this.title = title;
        this.status = status;
    }
}
