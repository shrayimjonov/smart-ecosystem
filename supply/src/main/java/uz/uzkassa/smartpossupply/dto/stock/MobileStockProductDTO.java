package uz.uzkassa.smartpossupply.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 24.02.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobileStockProductDTO implements Serializable {

    CommonDTO stock;

    StockStatus status;

    CompanyBaseDTO customer;

    CommonDTO branch;

    CommonDTO warehouse;

    ProductCommonDTO product;

    BigDecimal qty;

    BigDecimal price;

    BigDecimal priceWithoutVat;

    BigDecimal priceWithVat;

    BigDecimal discountPercent;

    BigDecimal discountPrice;

    Vat vat;

    BigDecimal sellingPrice;

    CompanyBaseDTO committent;

    CommonDTO productContract;

    CommonDTO serial;
}
