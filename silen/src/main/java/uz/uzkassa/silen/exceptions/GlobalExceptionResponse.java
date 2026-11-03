package uz.uzkassa.silen.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class GlobalExceptionResponse {
    private String title;
    private Integer status;
    private String detail;
    private String path;
    private String message;
    private String code;
    private Integer errorCode;
    private Map<String, Object> errors;
}
