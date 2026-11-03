package uz.uzkassa.smartpossupply.dto.productcontract;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.07.2023
 */

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductContractListDTO extends CommonDTO {

    BigDecimal basePrice;

    BigDecimal price;

    BigDecimal profitRate;

    ProductCommonDTO product;

    CommonDTO branch;

    BigDecimal qty;
}
