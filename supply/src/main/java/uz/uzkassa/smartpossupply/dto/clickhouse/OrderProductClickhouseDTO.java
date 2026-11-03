package uz.uzkassa.smartpossupply.dto.clickhouse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.05.2023 14:54
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderProductClickhouseDTO {

    Long id;

    Long productId;
    String productName;
    String productBarcode;
    String productVatBarcode;

    Long unitId;
    String unitName;

    BigDecimal qty;
    BigDecimal price;
    BigDecimal totalPrice;
    BigDecimal vatRate;
    BigDecimal vatPrice;
    BigDecimal totalVatPrice;
    BigDecimal priceListPrice;
    BigDecimal exciseRate;
    BigDecimal exciseSum;

    Long orderId;
    String orderNumber;
    String orderDate;
}
