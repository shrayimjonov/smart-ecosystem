package uz.uzkassa.smartpossupply.dto.order.terminal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.discount.DiscountListDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderProductBaseDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 08.10.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalOrderProductDetailDTO extends OrderProductBaseDTO {

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

    String barcode;

    String catalogCode;

    BigDecimal totalDiscountPrice;

    BigDecimal discountPercent;

    BigDecimal discountPrice;

    DiscountListDTO discount;

    String discountProductUniqueId;

    String parentDiscountProductUniqueId;

    @Schema
    List<TerminalOrderProductDetailDTO> discountProducts = new ArrayList<>();
}
