package uz.uzkassa.smartpos.dto.receipt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.ReceiptStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditPaymentDTO {
    private BigDecimal paid;
    private BigDecimal nds;
    private BigDecimal discount;
    private ReceiptStatus status;
    private LocalDateTime paidDateTime;
    private Map<PaymentType, BigDecimal> payments;
}
