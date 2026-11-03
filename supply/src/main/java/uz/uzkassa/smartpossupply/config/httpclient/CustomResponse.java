package uz.uzkassa.smartpossupply.config.httpclient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CustomResponse {
    private int code;
    private String content;
    private String errorMessage;
}
