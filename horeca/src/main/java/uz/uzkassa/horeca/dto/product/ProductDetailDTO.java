package uz.uzkassa.horeca.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.category.CategoryLookUpDTO;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.ProductOrigin;
import uz.uzkassa.horeca.enums.ProductType;
import uz.uzkassa.horeca.enums.Vat;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 836726L;

    String sku;

    String barcode;

    CategoryLookUpDTO category;

    String catalogName;

    String catalogCode;

    String packageName;

    String packageCode;

    Vat vat;

    BigDecimal price;

    BigDecimal weight;

    @JsonProperty("hasMark")
    boolean hasMark;

    String description;

    CommonDTO department;

    CommonDTO branch;

    FileDTO photo;

    CommonDTO unit;

    ProductOrigin origin;

    ProductType productType;

    @JsonProperty("hasTechCard")
    boolean hasTechCard;

    CommonStatus status;
}
