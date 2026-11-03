package uz.uzkassa.smartpossupply.filters;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.CurrencyType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReconciliationActFilter extends SimpleFilter {
    @NotNull(message = "{customer.must.have}")
    Long customerId;

    Long customerBranchId;

    Long stockOutId;

    CurrencyType currencyType;
}
