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
 * Date: 07.01.2025
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountCalculationDTO implements Serializable {

    Long productId;

    BigDecimal qty;

    BigDecimal totalPrice;

    BigDecimal exciseRate;

    BigDecimal exciseSum;

    Long warehouseId;
}