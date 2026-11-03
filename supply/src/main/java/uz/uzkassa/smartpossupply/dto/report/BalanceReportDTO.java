package uz.uzkassa.smartpossupply.dto.report;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductPhotoListDTO;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.12.2023
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class BalanceReportDTO implements Serializable {

    Long id;

    ProductCommonDTO product;

    BigDecimal qty;

    BigDecimal reservationQty;

    CommonDTO branch;

    CommonDTO warehouse;

    BigDecimal price;

    BigDecimal totalPrice;

    BigDecimal qtyInPackage;
    BigDecimal qtyInBox;
    BigDecimal qtyInPallet;

    boolean hasMark;

    String capacity;

    ProductType productType;

    ProductCommonDTO parent;

    String boxBarcode;

    String blockBarcode;

    Vat vat;

    BigDecimal brutto;

    BigDecimal netto;

    List<ProductPhotoListDTO> photos = new ArrayList<>();

    public void addPhoto(ProductPhotoListDTO photo) {
        photos.add(photo);
    }
}