package uz.uzkassa.smartpossupply.dto.stock;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 17.10.2024
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalStockProductBaseDTO implements Serializable {

    Long stockProductId;

    Long id; //productId

    ProductType productType;

    boolean hasMark;

    boolean hasMarking;

    BigDecimal qty; //soni

    BigDecimal sellingPrice; // 1 donasini narxi. QQS bilan

    BigDecimal sellingPriceWithoutVat; // 1 donasini narxi QQS siz

    BigDecimal totalSellingPrice; // qty * sellingPrice // n tasini narxi QQS bilan

    BigDecimal totalSellingPriceWithoutVat; // qty * sellingPriceWithoutVat // n tasini narxi QQS siz

    BigDecimal vatRate; //13% 15%

    BigDecimal vatPrice; //1 donasini QQS

    BigDecimal totalVatPrice; //qty * vatPrice // n tasini QQS

    BigDecimal basePrice;
}
