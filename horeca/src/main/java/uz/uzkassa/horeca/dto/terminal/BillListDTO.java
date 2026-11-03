package uz.uzkassa.horeca.dto.terminal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.terminal.BillStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillListDTO implements Serializable {

    Long id;

    String billId;

    BillStatus status;

    LocalDateTime time;

    String epsQrCode;

    BigDecimal amount;

    public BillListDTO(Long id, String billId, BillStatus status, LocalDateTime time, String epsQrCode, BigDecimal amount) {
        this.id = id;
        this.billId = billId;
        this.status = status;
        this.time = time;
        this.epsQrCode = epsQrCode;
        this.amount = amount;
    }
}
