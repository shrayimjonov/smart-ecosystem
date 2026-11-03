package uz.uzkassa.smartpossupply.dto.discount;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

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
public class DiscountItemProductDTO implements Serializable {

    Long id;

    BigDecimal amount;

    BigDecimal recommendedQty;

    BigDecimal limit;

    String valueType;

    BigDecimal productPrice;
}