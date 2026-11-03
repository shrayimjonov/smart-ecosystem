package uz.uzkassa.cts.dto.customerbranchdevice;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.PaymentType;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerBranchDeviceRequestDTO implements Serializable {

    @NotNull
    Long customerId;

    @NotNull
    Long deviceId;

    Long bankTariffId;

    @NotNull
    private Set<PaymentType> paymentTypes;
}
