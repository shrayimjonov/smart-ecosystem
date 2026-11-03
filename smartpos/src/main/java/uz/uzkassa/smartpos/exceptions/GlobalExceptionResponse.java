package uz.uzkassa.smartpos.exceptions;

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
    private Integer errorCode;
    private String solution;
    private Map<String, Object> errors;
}
