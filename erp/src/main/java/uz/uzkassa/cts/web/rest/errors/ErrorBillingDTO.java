package uz.uzkassa.cts.web.rest.errors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ErrorBillingDTO implements Serializable {

    String type;

    String title;

    Integer status;

    String detail;

    String path;

    String message;
}
