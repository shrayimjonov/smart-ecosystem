package uz.uzkassa.smartpossupply.dto.order.mobile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderProductBaseDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;
import uz.uzkassa.smartpossupply.enums.DiscountValueType;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 11:52
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobileOrderProductDetailDTO extends OrderProductBaseDTO {

    Long orderProductId;

    Long id;

    String name;

    UnitDTO unit;

    BigDecimal qtyInPackage;
    BigDecimal qtyInBox;
    BigDecimal qtyInPallet;

    String capacity;

    ProductType productType;

    Vat vat;

    BigDecimal brutto;

    BigDecimal netto;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    BigDecimal qtyInWarehouse;

    BigDecimal reservationQty;

    CommonDTO discount;

    CommonDTO discountItemProduct;

    BigDecimal totalDiscountPrice;

    BigDecimal discountPercent;

    BigDecimal discountPrice;

    DiscountValueType discountValueType;

    Integer totalQtyInPackage;

    String discountProductUniqueId;

    String parentDiscountProductUniqueId;

    BigDecimal discountLimit;

    @Schema
    List<MobileOrderProductDetailDTO> discountProducts = new ArrayList<>();
}
