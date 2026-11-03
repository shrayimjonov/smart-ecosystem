package uz.uzkassa.smartpossupply.integration.billing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillingExceptionDTO implements Serializable {
    String type;
    String title;
    String status;
    String path;
    String message;
    String detail;
}
