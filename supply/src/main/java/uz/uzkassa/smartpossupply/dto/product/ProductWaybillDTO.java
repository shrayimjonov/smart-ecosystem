package uz.uzkassa.smartpossupply.dto.product;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductWaybillDTO extends CommonDTO {
    BigDecimal qty;
    UnitDTO unit;
    BigDecimal sellingPrice;
    BigDecimal sellingPriceWithoutVat;
    BigDecimal totalSellingPrice;
    BigDecimal totalSellingPriceWithoutVat;
    BigDecimal vatRate;
    BigDecimal vatPrice;
    BigDecimal discountPrice;
    BigDecimal totalDiscountPrice;
    BigDecimal discountPercent;
}