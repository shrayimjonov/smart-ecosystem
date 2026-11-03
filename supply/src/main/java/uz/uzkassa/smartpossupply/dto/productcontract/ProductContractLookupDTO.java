package uz.uzkassa.smartpossupply.dto.productcontract;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.07.2023
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductContractLookupDTO extends CommonDTO {

    BigDecimal profitRate;

    BigDecimal basePrice;

    BigDecimal price;

    BigDecimal qty;
}
