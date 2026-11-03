package uz.uzkassa.smartpossupply.dto.stockreport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class StockReportByProductDTO {
    private Long productId;
    private String productName;
    private String barcode;
    private BigDecimal totalBrutto;
    private BigDecimal totalNetto;
    private BigDecimal totalQty;
    private BigDecimal totalQtyInBox;
    private BigDecimal totalSellingPrice;
    private BigDecimal totalPurchasePrice;
    private Long totalCustomerCount;
    String[] regions;
}
