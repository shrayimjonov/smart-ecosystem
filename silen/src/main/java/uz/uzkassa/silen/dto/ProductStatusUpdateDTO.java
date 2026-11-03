package uz.uzkassa.silen.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.ProductStatus;

import java.io.Serializable;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Product} entity.
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductStatusUpdateDTO implements Serializable {
    @NotNull(message = "ID отсуствует")
    String id;

    @NotNull
    ProductStatus status;
}
