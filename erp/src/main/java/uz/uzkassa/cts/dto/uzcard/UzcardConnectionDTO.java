package uz.uzkassa.cts.dto.uzcard;

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
public class UzcardConnectionDTO implements Serializable {

    boolean deleted;

    String serialNum;

    String merchantName;

    String terminalType;

    String model;
}
