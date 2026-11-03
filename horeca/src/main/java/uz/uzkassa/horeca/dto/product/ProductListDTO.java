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

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductListDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1244009L;

    String sku;

    CategoryLookUpDTO category;

    BigDecimal price;

    FileDTO photo;

    CommonDTO unit;

    CommonStatus status;

    String barcode;

    String catalogCode;

    @JsonProperty("hasMark")
    boolean hasMark;

    boolean hasTechCard;
}
