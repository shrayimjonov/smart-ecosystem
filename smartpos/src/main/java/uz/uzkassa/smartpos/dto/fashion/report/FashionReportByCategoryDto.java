package uz.uzkassa.smartpos.dto.fashion.report;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FashionReportByCategoryDto {
    private Long categoryId;
    private Long salesCount = 0L;
    private BigDecimal totalDiscount = BigDecimal.ZERO;
    private BigDecimal totalVat = BigDecimal.ZERO;
    private BigDecimal totalSumma = BigDecimal.ZERO;
    private Long salesReceiptsCount = 0L;
    private Long returnedReceiptsCount = 0L;
}
