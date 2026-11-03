package uz.uzkassa.horeca.dto.receipt.cabinet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.ReceiptPaymentType;
import uz.uzkassa.horeca.enums.ReceiptStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiptListCabinetDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 58498549L;

    Long id;

    LocalDateTime receiptDate;

    BigDecimal totalCost;

    BigDecimal totalVat;

    BigDecimal totalCash;

    ReceiptStatus status;

    String terminalSerialNumber;

    Long shiftNumber;

    CommonDTO hall;

    CommonDTO table;

    Map<ReceiptPaymentType, BigDecimal> payments;
}
