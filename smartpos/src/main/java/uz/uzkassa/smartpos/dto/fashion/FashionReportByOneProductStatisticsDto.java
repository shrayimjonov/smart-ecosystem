package uz.uzkassa.smartpos.dto.fashion;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FashionReportByOneProductStatisticsDto {
    private BigDecimal totalDiscount;
    private BigDecimal totalSum;
    private BigDecimal totalPaidProductCount;
    private BigDecimal totalReceiptCount;
}
