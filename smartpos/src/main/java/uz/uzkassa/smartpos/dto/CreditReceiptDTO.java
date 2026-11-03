package uz.uzkassa.smartpos.dto;

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
public class CreditReceiptDTO {
    private String uid;
    private String originUid;
    private BigDecimal totalPaid;
    private Map<PaymentType, BigDecimal> payments;
    private LocalDateTime receiptDateTime;
    private ReceiptStatus status;
}
