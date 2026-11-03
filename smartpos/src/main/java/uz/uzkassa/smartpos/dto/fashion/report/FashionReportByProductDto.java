package uz.uzkassa.smartpos.dto.fashion.report;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FashionReportByProductDto {
    private Long productId;
    private String productName;
    private String categoryName;
    private BigDecimal avgSalesPrice;
    private Long salesCount;
    private BigDecimal totalDiscount;
    private BigDecimal totalVat;
    private BigDecimal totalSumma;
    private Long salesReceiptsCount;
    private Long returnedReceiptsCount;
}
