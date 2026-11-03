package uz.uzkassa.smartpos.dto.receipt;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ScheduleDTO {
    private BigDecimal totalPaid;
    private BigDecimal totalCost;
    private BigDecimal totalPaidNds;
    private BigDecimal totalNds;
    private BigDecimal totalDiscount = BigDecimal.ZERO;
    private BigDecimal discountPercent = BigDecimal.ZERO;
    private List<CreditBillDTO> bills;
}
