package uz.uzkassa.smartpossupply.dto.stockreport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockReportDTO extends StockRevenueReport {

    BigDecimal totalProfit;

    BigDecimal soldQty;

    Long markUpPercent;

    public StockReportDTO(BigDecimal totalProfit, BigDecimal soldQty, Long markUpPercent) {
        this.totalProfit = totalProfit;
        this.soldQty = soldQty;
        this.markUpPercent = markUpPercent;
    }

    public StockReportDTO(
        Long id,
        String name,
        BigDecimal qty,
        BigDecimal sellingPrice,
        BigDecimal totalSellingPrice,
        BigDecimal purchasePrice,
        BigDecimal totalPurchasePrice,
        BigDecimal totalProfit,
        BigDecimal soldQty,
        Long markUpPercent
    ) {
        super(id, name, qty, sellingPrice, totalSellingPrice, purchasePrice, totalPurchasePrice);
        this.totalProfit = totalProfit;
        this.soldQty = soldQty;
        this.markUpPercent = markUpPercent;
    }

    public StockReportDTO(
        Long id,
        String name,
        BigDecimal sellingPrice,
        BigDecimal totalSellingPrice,
        BigDecimal purchasePrice,
        BigDecimal totalPurchasePrice,
        BigDecimal potentialProfit,
        BigDecimal potentialProfitPercent,
        BigDecimal totalProfit,
        BigDecimal soldQty,
        Long markUpPercent
    ) {
        super(id, name, sellingPrice, totalSellingPrice, purchasePrice, totalPurchasePrice, potentialProfit, potentialProfitPercent);
        this.totalProfit = totalProfit;
        this.soldQty = soldQty;
        this.markUpPercent = markUpPercent;
    }
}
