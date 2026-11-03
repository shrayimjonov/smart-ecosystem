package uz.uzkassa.cts.dto.bill;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillConfirmDTO implements Serializable {
    @NotNull
    String billId;
    String confirmationKey;
}
