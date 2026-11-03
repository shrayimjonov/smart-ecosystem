package uz.uzkassa.horeca.web.rest.errors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO implements Serializable {

    String title;

    String detail;

    Integer status;

    Integer customStatus;

    String timestamp;

    String path;

    String url;

    String message;

    Integer errorCode;

    List<FieldErrorVM> errorFields;

    Map<String, Object> errors;
}
