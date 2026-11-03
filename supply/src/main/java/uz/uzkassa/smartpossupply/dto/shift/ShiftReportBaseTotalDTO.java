package uz.uzkassa.smartpossupply.dto.shift;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.receipt.ReceiptBaseTotalDTO;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ShiftReportBaseTotalDTO extends ReceiptBaseTotalDTO {

    BigDecimal totalCashReturned;

    BigDecimal totalCardReturned;

    BigDecimal totalVatReturned;

    Long receiptCount;

    Long receiptReturnedCount;
}
