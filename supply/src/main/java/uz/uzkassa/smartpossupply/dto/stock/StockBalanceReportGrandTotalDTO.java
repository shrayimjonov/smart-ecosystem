package uz.uzkassa.smartpossupply.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("unused")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockBalanceReportGrandTotalDTO {

    BigDecimal qty;
    BigDecimal totalSellingPrice;
    BigDecimal totalPurchasePrice;
    BigDecimal potentialProfit;

    //todo Note: it is used on stock balance report grand total query
    public StockBalanceReportGrandTotalDTO(
        BigDecimal qty,
        BigDecimal totalSellingPrice,
        BigDecimal totalPurchasePrice,
        BigDecimal potentialProfit
    ) {
        this.qty = qty;
        this.totalSellingPrice = totalSellingPrice;
        this.totalPurchasePrice = totalPurchasePrice;
        this.potentialProfit = potentialProfit;
    }
}
