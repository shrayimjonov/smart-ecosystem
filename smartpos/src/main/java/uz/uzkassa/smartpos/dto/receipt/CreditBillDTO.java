package uz.uzkassa.smartpos.dto.receipt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditBillDTO {
    private LocalDateTime dueDateTime;
    private BigDecimal dueAmount;
    private BigDecimal dueNdsAmount;
    private List<LocalDateTime> payedDateTimes;
    private BigDecimal paidAmount;
    private BigDecimal paidNdsAmount;
    private BigDecimal discount;
    private Integer paidCount;
    private Map<PaymentType, BigDecimal> payments;
}
