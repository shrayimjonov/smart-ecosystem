package uz.uzkassa.horeca.dto.terminal;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.terminal.BillStatus;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApayCreateBillResponse implements Serializable {
    String id;
    BillStatus status;
    LocalDateTime time;
    String providerId;
    String epsBillId;
    String epsQrCode;
}
