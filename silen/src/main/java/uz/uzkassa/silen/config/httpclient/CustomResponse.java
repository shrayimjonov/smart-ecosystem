package uz.uzkassa.silen.config.httpclient;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
public class CustomResponse {
    private int code;
    private String content;
    private String errorMessage;
}
