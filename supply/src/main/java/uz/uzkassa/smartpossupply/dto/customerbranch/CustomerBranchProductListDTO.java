package uz.uzkassa.smartpossupply.dto.customerbranch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 30.11.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerBranchProductListDTO {

    Long id;

    CommonDTO customerBranch;

    CommonDTO product;

    CommonDTO unit;

    BigDecimal qty;

    BigDecimal price;
}
