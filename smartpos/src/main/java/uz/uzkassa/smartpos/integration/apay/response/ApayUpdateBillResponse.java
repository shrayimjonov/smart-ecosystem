package uz.uzkassa.smartpos.integration.apay.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApayUpdateBillResponse implements Serializable {
    String billId;
    boolean smsSent = false;
    boolean tryPin = false; //NFC pin
}
