package uz.uzkassa.smartpossupply.dto.productcontract;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.07.2023
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductContractDTO implements Serializable {

    Long id;

    String name;

    BigDecimal basePrice;

    BigDecimal price;

    BigDecimal profitRate;

    Long productId;

    Long branchId;
}
