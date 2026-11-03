package uz.uzkassa.smartpossupply.web.rest.errors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntegrationExceptionDTO extends FacturaErrorDTO {

    String timestamp;

    Integer status;

    String error;

    String message;

    String path;

    String title;

    String detail;

    String error_message;

    String error_description;

    public String getDetail() {
        if (StringUtils.isNotEmpty(this.errorMessage)) {
            return this.errorMessage;
        }
        if (StringUtils.isNotEmpty(detail)) {
            return detail;
        }
        if (StringUtils.isNotEmpty(error)) {
            return error;
        }
        if (StringUtils.isNotEmpty(message)) {
            return message;
        }
        if (StringUtils.isNotEmpty(title)) {
            return title;
        }
        if (StringUtils.isNotEmpty(error_message)) {
            return error_message;
        }
        if (StringUtils.isNotEmpty(error_description)) {
            return error_description;
        }
        return null;
    }
}
