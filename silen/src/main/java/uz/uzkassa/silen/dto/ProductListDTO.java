package uz.uzkassa.silen.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.ProductStatus;
import uz.uzkassa.silen.enumeration.ProductType;

import java.io.Serializable;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Product} entity.
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductListDTO implements Serializable {

    String id;

    @NotNull(message = "Наименование отсуствует")
    String name;

    @Size(min = 8, max = 14, message = "Gtin должен содержать не менее 8 не более  14 символов.")
    String barcode;

    String parentBarcode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String organizationId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String organizationTin;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String organizationName;

    String catalogCode;

    ProductType type;

    String typeName;

    ProductStatus status;

    String statusName;

}
