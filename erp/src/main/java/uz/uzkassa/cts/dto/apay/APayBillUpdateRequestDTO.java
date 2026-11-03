package uz.uzkassa.cts.dto.apay;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class APayBillUpdateRequestDTO implements Serializable {
    String billId;
    String pan;
    String expiry;
}
