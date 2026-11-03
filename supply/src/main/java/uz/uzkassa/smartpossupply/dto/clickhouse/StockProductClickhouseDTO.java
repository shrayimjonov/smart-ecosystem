package uz.uzkassa.smartpossupply.dto.clickhouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 01.05.2023 16:46
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockProductClickhouseDTO extends AuditClickhouseDTO {

    Long id;

    Long productId;
    String productName;
    String productBarcode;
    String productVatBarcode;

    Long unitId;

    BigDecimal qty;
    BigDecimal qtyInPackage;
    Integer aggregationQty;
    Integer markQty;
    Integer aggregationBlockQty;
    BigDecimal sellingPrice;
    BigDecimal sellingPriceWithoutVat;
    BigDecimal totalSellingPrice;
    BigDecimal totalSellingPriceWithoutVat;
    BigDecimal purchasePrice;
    BigDecimal purchasePriceWithoutVat;
    BigDecimal totalPurchasePrice;
    BigDecimal totalPurchasePriceWithoutVat;
    BigDecimal vatRate;
    BigDecimal vatPrice;
    BigDecimal totalVatPrice;
    BigDecimal exciseRate;
    BigDecimal exciseSum;
    BigDecimal markupPercent;
    BigDecimal markupPrice;
    BigDecimal priceListPrice;
    boolean hasMarking;

    Long stockId;
    String stockNumber;
    String stockTransferDate;
}
