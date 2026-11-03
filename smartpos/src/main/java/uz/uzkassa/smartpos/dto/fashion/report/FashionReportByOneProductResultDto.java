package uz.uzkassa.smartpos.dto.fashion.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FashionReportByOneProductResultDto {
    private Long colorGroupId;
    private String colorCode;
    private String colorName;
    private String printName;
    private Long branchId;
    private String branchName;
    private BigDecimal avgSalesPrice = BigDecimal.ZERO;
    private Long salesCount = 0L;
    private BigDecimal totalDiscount = BigDecimal.ZERO;
    private BigDecimal totalVat = BigDecimal.ZERO;
    private BigDecimal totalSumma = BigDecimal.ZERO;
    private Long salesReceiptsCount = 0L;
    private Long returnedReceiptsCount = 0L;
    private List<FashionReportByOneProductItemDto> items = new ArrayList<>();

    public FashionReportByOneProductResultDto(FashionReportByOneProductDto report) {
        this.colorGroupId = report.getColorGroupId();
        this.colorCode = report.getColorCode();
        this.colorName = report.getColorName();
        this.printName = report.getPrintName();
        this.branchId = report.getBranchId();
        this.branchName = report.getBranchName();
    }

    public void addItem(FashionReportByOneProductItemDto item) {
        items.add(item);
        this.avgSalesPrice = item.getAvgSalesPrice();
        this.salesCount += item.getSalesCount();
        this.totalDiscount = this.totalDiscount.add(item.getTotalDiscount());
        this.totalVat = this.totalVat.add(item.getTotalVat());
        this.totalSumma = this.totalSumma.add(item.getTotalSumma());
        this.salesReceiptsCount += item.getSalesReceiptsCount();
        this.returnedReceiptsCount += item.getReturnedReceiptsCount();
    }
}
