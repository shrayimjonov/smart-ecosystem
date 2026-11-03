package uz.uzkassa.smartpossupply.dto.order.mobile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.order.OrderProductBaseDTO;
import uz.uzkassa.smartpossupply.dto.reconciliationact.PackageQuantityDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 12:17
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobileOrderProductDTO extends OrderProductBaseDTO {

    Long orderProductId;

    Long id;

    Long unitId;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    Long discountId;

    Long discountItemProductId;

    BigDecimal discountPercent;

    BigDecimal discountPrice;

    BigDecimal totalDiscountPrice;

    String discountProductUniqueId;

    String parentDiscountProductUniqueId;

    List<PackageQuantityDTO> packageQuantities;
}
