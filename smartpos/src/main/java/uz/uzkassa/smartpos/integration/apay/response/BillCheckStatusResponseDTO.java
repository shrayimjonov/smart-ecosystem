package uz.uzkassa.smartpos.integration.apay.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillCheckStatusResponseDTO implements Serializable {
    String code;
    String name;
    String clientPhoneNumber;
    String message;
    LocalDateTime time;
    String epsBillId;
}
