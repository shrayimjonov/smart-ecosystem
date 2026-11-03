package uz.uzkassa.smartpossupply.dto.customerbranch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 30.11.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerBranchProductItemDTO {

    Long id;

    Long productId;

    Long unitId;

    BigDecimal qty;

    BigDecimal price;
}
