package uz.uzkassa.smartpossupply.dto.productcontract;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.07.2023
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductContractDetailDTO extends CommonDTO {

    BigDecimal basePrice;

    BigDecimal price;

    BigDecimal profitRate;

    CommonDTO product;

    CommonDTO branch;
}
