package uz.uzkassa.horeca.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.ProductOrigin;
import uz.uzkassa.horeca.enums.ProductType;
import uz.uzkassa.horeca.enums.Vat;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductLookUpDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4576864L;

    Long id;

    String name;

    String sku;

    String barcode;

    String packageCode;

    String packageName;

    String catalogCode;

    String catalogName;

    Vat vat;

    BigDecimal price;

    CommonDTO unit;

    ProductOrigin origin;

    CommonDTO category;

    ProductType productType;

    @JsonProperty("hasMark")
    boolean hasMark;

    boolean hasTechCard;
}
