package uz.uzkassa.smartpos.dto.receipt;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.ReceiptStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@ToString
public class TransitReceiptDTO implements Serializable {
    private String uid;
    private String originUid;
    @Size(max = 255)
    private String fiscalUrl;
    @Size(max = 255)
    private String fiscalSign;
    private ReceiptStatus status;
    private LocalDateTime receiptDateTime;
    private LocalDateTime createdDateTime;
    private String facturaId;
    private Map<PaymentType, BigDecimal> payments;
    private BigDecimal totalCash = BigDecimal.ZERO;
    private BigDecimal totalCard = BigDecimal.ZERO;
    private BigDecimal totalLoyaltyCard = BigDecimal.ZERO;
    @NotNull
    private BigDecimal totalCost;
    private BigDecimal totalDiscount;
    private BigDecimal totalExcise;
    private BigDecimal discountPercent;
    @NotNull
    private BigDecimal totalPaid;
    @NotNull
    private BigDecimal totalNds = BigDecimal.ZERO;

}
