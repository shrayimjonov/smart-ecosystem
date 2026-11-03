package uz.uzkassa.smartpossupply.dto.stockout;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalStockOutProductDTO {

    Long stockProductId;

    Long id; //productId

    boolean hasMarking;

    BigDecimal qty; //soni

    BigDecimal sellingPrice; // 1 donasini narxi. QQS bilan

    BigDecimal vatRate;

    Long unitId;

    Long discountId;

    Long discountItemProductId;

    String discountProductUniqueId;

    String parentDiscountProductUniqueId;

    BigDecimal discountPercent;

    BigDecimal discountPrice;

    BigDecimal totalDiscountPrice;

    BigDecimal exciseRate;

    BigDecimal exciseSum;
}
