package uz.uzkassa.horeca.dto.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.ProductOrigin;
import uz.uzkassa.horeca.enums.ProductType;
import uz.uzkassa.horeca.enums.Vat;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 38495843L;

    Long id;

    String name;

    String sku;

    String barcode;

    Long categoryId;

    String catalogName;

    String catalogCode;

    Vat vat;

    BigDecimal price;

    BigDecimal weight;

    @JsonProperty("hasMark")
    boolean hasMark;

    String description;

    Long departmentId;

    Long branchId;

    Long unitId;

    String packageName;

    String packageCode;

    ProductOrigin origin;

    ProductType productType;

    @JsonProperty("hasTechCard")
    boolean hasTechCard;

    @JsonIgnore
    UUID uniqueId;
}
