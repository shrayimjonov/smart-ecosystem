package uz.uzkassa.smartpossupply.dto.stockreport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockRevenueReport extends CommonDTO {

    private BigDecimal qty;

    String barcode;

    String unit;

    String companyName;

    Long companyId;

    BigDecimal sellingPrice; // price 1PC. 1 donasini narxi

    BigDecimal totalSellingPrice; // qty * sellingPrice // sotish narxi

    BigDecimal purchasePrice; //price 1PC. 1 donasini narxi

    BigDecimal totalPurchasePrice; // qty * purchasePrice // sotib olish narxi

    BigDecimal potentialProfit;

    BigDecimal potentialProfitPercent;

    //Note this constructor is used on stock report
    public StockRevenueReport(
        Long id,
        String name,
        BigDecimal qty,
        BigDecimal sellingPrice,
        BigDecimal totalSellingPrice,
        BigDecimal purchasePrice,
        BigDecimal totalPurchasePrice
    ) {
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.sellingPrice = sellingPrice;
        this.totalSellingPrice = totalSellingPrice;
        this.purchasePrice = purchasePrice;
        this.totalPurchasePrice = totalPurchasePrice;
    }

    //Note this constructor is used on stock report
    public StockRevenueReport(
        Long id,
        String name,
        BigDecimal qty,
        String barcode,
        String unit,
        String companyName,
        Long companyId,
        BigDecimal sellingPrice,
        BigDecimal totalSellingPrice,
        BigDecimal purchasePrice,
        BigDecimal totalPurchasePrice
    ) {
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.barcode = barcode;
        this.unit = unit;
        this.companyName = companyName;
        this.companyId = companyId;
        this.sellingPrice = sellingPrice;
        this.totalSellingPrice = totalSellingPrice;
        this.purchasePrice = purchasePrice;
        this.totalPurchasePrice = totalPurchasePrice;
    }

    public StockRevenueReport(
        Long id,
        String name,
        BigDecimal sellingPrice,
        BigDecimal totalSellingPrice,
        BigDecimal purchasePrice,
        BigDecimal totalPurchasePrice,
        BigDecimal potentialProfit,
        BigDecimal potentialProfitPercent
    ) {
        this.id = id;
        this.name = name;
        this.sellingPrice = sellingPrice;
        this.totalSellingPrice = totalSellingPrice;
        this.purchasePrice = purchasePrice;
        this.totalPurchasePrice = totalPurchasePrice;
        this.potentialProfit = potentialProfit;
        this.potentialProfitPercent = potentialProfitPercent;
    }
}
