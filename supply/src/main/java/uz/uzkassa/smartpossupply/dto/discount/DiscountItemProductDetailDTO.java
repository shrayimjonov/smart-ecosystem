package uz.uzkassa.smartpossupply.dto.discount;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.enums.DiscountValueType;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.12.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountItemProductDetailDTO implements Serializable {

    Long discountItemProductId;

    ProductCommonDTO product;

    BigDecimal productPrice;

    BigDecimal amount;

    BigDecimal recommendedQty;

    DiscountValueType valueType;

    BigDecimal limit;
}