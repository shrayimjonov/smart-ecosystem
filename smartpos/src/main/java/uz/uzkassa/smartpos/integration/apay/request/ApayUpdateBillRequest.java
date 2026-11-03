package uz.uzkassa.smartpos.integration.apay.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApayUpdateBillRequest implements Serializable {
    @NotNull
    String billId;
    String clientId;
    String phone;
    String nfc;
    String pan;
    String expiry;
}
