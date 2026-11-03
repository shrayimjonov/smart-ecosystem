package uz.uzkassa.silen.dto.warehouse;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.domain.Customer;

import java.io.Serializable;

/**
 * A DTO for the {@link Customer} entity.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidateMarkDTO implements Serializable {

    String code;

    String errorMessage;
}
