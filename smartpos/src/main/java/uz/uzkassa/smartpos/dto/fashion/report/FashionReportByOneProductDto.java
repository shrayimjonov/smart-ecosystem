package uz.uzkassa.smartpos.dto.fashion.report;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FashionReportByOneProductDto {
    private Long colorGroupId;
    private String colorCode;
    private String colorName;
    private String printName;
    private Long itemId;
    private String sizeCode;
    private String sizeValue;
    private BigDecimal avgSalesPrice;
    private Long salesCount;
    private BigDecimal totalDiscount;
    private BigDecimal totalVat;
    private BigDecimal totalSumma;
    private Long salesReceiptsCount;
    private Long returnedReceiptsCount;
    private Long branchId;
    private String branchName;
}
