package uz.uzkassa.smartpos.dto.fashion.report;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FashionReportByUserStatisticsDto {
    private BigDecimal totalSumma = BigDecimal.ZERO;
    private BigDecimal totalDiscount = BigDecimal.ZERO;
    private Long receiptsCount = 0L;
}
