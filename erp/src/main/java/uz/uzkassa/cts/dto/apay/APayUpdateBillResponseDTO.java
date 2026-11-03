package uz.uzkassa.cts.dto.apay;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class APayUpdateBillResponseDTO implements Serializable {
    String billId;
    boolean smsSent = false;
    boolean tryPin = false;
}
