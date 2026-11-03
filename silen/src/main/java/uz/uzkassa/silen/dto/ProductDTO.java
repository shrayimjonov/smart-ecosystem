package uz.uzkassa.silen.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.ProductStatus;
import uz.uzkassa.silen.enumeration.ProductType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Product} entity.
 */
@ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDTO implements Serializable {
    String id;

    @NotNull(message = "Наименование отсуствует")
    String name;

    Integer code;

    String shortName;

    @Size(min = 8, max = 14, message = "Gtin должен содержать не менее 8 не более  14 символов.")
    String barcode;

    String parentBarcode;

    String catalogCode;

    String catalogName;

    String packageCode;

    String packageName;

    String gtin;

    @NotNull
    ProductType type;

    String organizationId;

    ProductStatus status;

    String statusName;

    BigDecimal price;

    BigDecimal basePrice;

    BigDecimal profitRate;

    BigDecimal exciseRate;

    BigDecimal vatRate;

    Set<ProductAttributesDTO> attributes;

    int qtyInAggregation;

    int qtyInBlock;

    int expiredInMonth;

    BigDecimal weightBrutto;

    BigDecimal weightNetto;
}
