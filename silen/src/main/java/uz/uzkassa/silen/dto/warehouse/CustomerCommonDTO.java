package uz.uzkassa.silen.dto.warehouse;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.domain.Customer;

import java.io.Serializable;

/**
 * A DTO for the {@link Customer} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerCommonDTO implements Serializable {

    Long id;

    @NotBlank
    String tin;

    String name;

    String description;
}
