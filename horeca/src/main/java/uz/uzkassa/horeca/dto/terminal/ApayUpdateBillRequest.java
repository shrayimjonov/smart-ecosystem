package uz.uzkassa.horeca.dto.terminal;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
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
