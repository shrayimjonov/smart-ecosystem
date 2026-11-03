package uz.uzkassa.smartpossupply.dto.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailMobileDTO extends CommonDTO {

    String barcode;

    ProductType productType;

    Vat vat;

    UnitDTO unit;

    BigDecimal price;

    BigDecimal exciseRate;

    BigDecimal exciseSum;

    String vatBarcode;
    String catalogName;

    String packageName;
    String packageCode;

    BigDecimal qtyInPackage;
    BigDecimal qtyInBox;
    BigDecimal qtyInPallet;

    String capacity;

    boolean isFavorite;

    List<ProductPhotoListDTO> photos = new ArrayList<>();

    public void addPhoto(ProductPhotoListDTO productPhotoListDTO) {
        photos.add(productPhotoListDTO);
    }
}
