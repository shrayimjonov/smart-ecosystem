package uz.uzkassa.smartpos.dto.receipt;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.ReceiptStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CreditAdvanceReceiptDTO {
    private Long id;
    private String uid;
    private String originUid;
    private String customerName;
    private String customerPhone;
    private LocalDateTime receiptDateTime;
    private ReceiptStatus receiptStatus;
    private BigDecimal totalCost;
    private BigDecimal totalPaid;
    private Integer paymentCount;
    private BigDecimal totalRemaining;
}
