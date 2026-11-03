package uz.uzkassa.silen.dto.warehouse;

import jakarta.validation.constraints.NotNull;
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
public class FinancialDiscountDTO implements Serializable {

    @NotNull
    Long customerId;

    boolean hasFinancialDiscount;
}
