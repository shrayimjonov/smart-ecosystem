package uz.uzkassa.smartpos.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

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

    public static ResponseData<?> ok() {
        return builder().statusCode(HttpStatus.OK.value()).build();
    }

    public static <T> ResponseData<T> ok(T data) {
        return new ResponseData<>(null, data, HttpStatus.OK.value());
    }

}
