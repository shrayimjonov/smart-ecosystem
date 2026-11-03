package uz.uzkassa.smartpossupply.dto.marketplacesettings;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PaymentType;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyPaymentSettingsDTO implements Serializable {

    @NotNull
    @NotEmpty
    Set<PaymentType> types;
}
