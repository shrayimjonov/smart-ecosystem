package uz.uzkassa.smartpossupply.dto.pricelist;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.02.2023 14:26
 */
@Getter
@Setter
public class PriceListItemDetailDTO {

    Long id;

    BigDecimal price;

    BigDecimal catalogPrice;

    CommonDTO product;
}
