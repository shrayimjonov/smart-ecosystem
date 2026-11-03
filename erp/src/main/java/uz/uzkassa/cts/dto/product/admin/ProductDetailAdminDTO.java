package uz.uzkassa.cts.dto.product.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.LocalizedDTO;
import uz.uzkassa.cts.dto.vat.VatItemDTO;
import uz.uzkassa.cts.enums.ProductType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailAdminDTO extends LocalizedDTO implements Serializable {

    ProductType type;

    String description;

    String barcode;

    String skuNumber;

    String upcNumber;

    String partNumber;

    String inventoryNumber;

    BigDecimal price;

    CommonDTO brand;

    CommonDTO category;

    VatItemDTO vat;

    CommonDTO unit;
}
