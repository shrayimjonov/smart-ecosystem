package uz.uzkassa.cts.web.rest.errors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ErrorDTO implements Serializable {

    String title;

    String detail;

    Integer status;

    Integer customStatus;

    String timestamp;

    String path;

    String url;

    String solution;

    String message;

    Integer errorCode;

    List<FieldErrorVM> errorFields;

    Map<String, Object> errors;

    String errorMessage;

    String error;

    public String getMessage() {
        if (StringUtils.isNotEmpty(this.message)) {
            return message;
        } else if (StringUtils.isNotEmpty(this.errorMessage)) {
            return errorMessage;
        } else if (StringUtils.isNotEmpty(this.error)) {
            return error;
        } else if (StringUtils.isNotEmpty(this.solution)) {
            return solution;
        } else if (StringUtils.isNotEmpty(this.detail)) {
            return detail;
        } else if (StringUtils.isNotEmpty(this.title)) {
            return title;
        }
        return null;
    }
}
