package uz.uzkassa.smartpossupply.dto.pricelist;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.02.2023 13:59
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceListItemDTO {

    Long id;

    BigDecimal price;

    Long productId;
}
