package uz.uzkassa.cts.dto.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.LocalizedDTO;
import uz.uzkassa.cts.enums.ProductType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDTO extends LocalizedDTO implements Serializable {

    ProductType type;

    String description;

    String barcode;

    String skuNumber;

    String upcNumber;

    String partNumber;

    String inventoryNumber;

    BigDecimal price;

    Long brandId;

    Long categoryId;

    Long vatId;

    Long unitId;
}
