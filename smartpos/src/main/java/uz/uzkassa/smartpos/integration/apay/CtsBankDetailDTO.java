package uz.uzkassa.smartpos.integration.apay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class CtsBankDetailDTO implements Serializable {

    String id;

    String name;

    String accountNumber;

    String mfo;

    String oked;

    String tin;

    CtsBankDetailDTO parent;
}
