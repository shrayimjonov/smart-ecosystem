package uz.uzkassa.smartpos.dto.fashion.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class FashionReportByOneProductItemDto {
    private String sizeValue;
    private Long salesCount;
    private BigDecimal avgSalesPrice;
    private BigDecimal totalSumma;
    private BigDecimal totalDiscount;
    private Long salesReceiptsCount;
    private BigDecimal totalVat;
    private Long returnedReceiptsCount;
    private Long itemId;
    private String sizeCode;

    public FashionReportByOneProductItemDto(FashionReportByOneProductDto report) {
        this.avgSalesPrice = report.getAvgSalesPrice();
        this.salesCount = report.getSalesCount();
        this.totalDiscount = report.getTotalDiscount();
        this.totalVat = report.getTotalVat();
        this.totalSumma = report.getTotalSumma();
        this.salesReceiptsCount = report.getSalesReceiptsCount();
        this.returnedReceiptsCount = report.getReturnedReceiptsCount();
        this.itemId = report.getItemId();
        this.sizeCode = report.getSizeCode();
        this.sizeValue = report.getSizeValue();
    }

}
