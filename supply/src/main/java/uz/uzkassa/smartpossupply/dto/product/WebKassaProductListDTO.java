package uz.uzkassa.smartpossupply.dto.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 15.12.2022 15:48
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebKassaProductListDTO  extends CommonDTO {

    Vat vat;

    UnitDTO unit;

    BigDecimal price;

    BigDecimal qtyInWarehouse;

    String barcode;

    String vatBarcode;

    boolean favorite;

    boolean hasMark;

    @SuppressWarnings("unused")
    public WebKassaProductListDTO(
        Long id,
        String name,
        String vat,
        Long unitId,
        String unitName,
        String unitCode,
        Long measureId,
        BigDecimal price,
        BigDecimal qtyInWarehouse,
        String barcode,
        String vatBarcode,
        boolean favorite,
        boolean hasMark
    ) {
        this.id = id;
        this.name = name;
        this.vat = Vat.valueOf(vat);
        this.unit = new UnitDTO(unitId, unitName, unitCode, measureId);
        this.price = price;
        this.qtyInWarehouse = qtyInWarehouse;
        this.barcode = barcode;
        this.vatBarcode = vatBarcode;
        this.favorite = favorite;
        this.hasMark = hasMark;
    }
}
