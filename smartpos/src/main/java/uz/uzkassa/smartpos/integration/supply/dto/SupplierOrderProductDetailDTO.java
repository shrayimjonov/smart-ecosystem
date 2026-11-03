package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.ProductType;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class SupplierOrderProductDetailDTO {
    Long orderProductId;

    Long id;

    String name;

    String barcode;

    String vatBarcode;

    UnitDTO unit;

    boolean hasMark;

    ProductType productType;

    BigDecimal qty;

    BigDecimal price;//price with NDS for 1x

    BigDecimal vatRate; //12%, 15%, null
}
