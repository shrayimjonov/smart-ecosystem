package uz.uzkassa.smartpossupply.dto.stockout;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 17.10.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalStockOutProductUpdateDTO implements Serializable {

    Long stockOutProductId;

    Long productId;

    BigDecimal qty;

    BigDecimal price;

    BigDecimal vatRate;

    Long discountId;

    Long discountItemProductId;

    BigDecimal discountPrice;

    BigDecimal discountPercent;

    String discountProductUniqueId;

    String parentDiscountProductUniqueId;

    BigDecimal exciseRate;

    BigDecimal exciseSum;
}
