package uz.uzkassa.horeca.dto.terminal;

import java.io.Serializable;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ResponseData<T> implements Serializable {
    private String message;
    private T data;
    private String errorMessage;
    private int statusCode;

    public ResponseData(String message, T data, int statusCode) {
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;
    }

    public ResponseData(String message, String errorMessage, int statusCode) {
        this.message = message;
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }

    public static ResponseData<?> ok() {
        return builder().statusCode(HttpStatus.OK.value()).build();
    }

    public static <T> ResponseData<T> ok(T data) {
        return new ResponseData<>(null, data, HttpStatus.OK.value());
    }

    public static <T> ResponseData<T> error(String message, String errorMessage) {
        return new ResponseData<>(message, errorMessage, HttpStatus.BAD_REQUEST.value());
    }

}
