package uz.uzkassa.smartpossupply.dto.stockreport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class StockReportByProductExtraInfoDTO {
    private Long productId;
    private String productName;
    private BigDecimal totalQty;
    private BigDecimal totalQtyWithDiscount;
    private BigDecimal totalQtyWithPriceList;
    private BigDecimal totalSellingPrice;
    private BigDecimal totalPurchasePrice;
    private Long totalCustomerCount;
}
