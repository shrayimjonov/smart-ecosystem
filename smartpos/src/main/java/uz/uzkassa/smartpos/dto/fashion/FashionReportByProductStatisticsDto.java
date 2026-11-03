package uz.uzkassa.smartpos.dto.fashion;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FashionReportByProductStatisticsDto {
    private BigDecimal totalDiscount;
    private BigDecimal totalNds;
    private BigDecimal totalProductCount;
    private BigDecimal totalSum;
}
