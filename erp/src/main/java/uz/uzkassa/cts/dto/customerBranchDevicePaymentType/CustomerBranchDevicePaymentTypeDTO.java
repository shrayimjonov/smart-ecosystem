package uz.uzkassa.cts.dto.customerBranchDevicePaymentType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.PaymentType;

import java.util.Set;

/**
 **/

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerBranchDevicePaymentTypeDTO {
    Long customerBranchDeviceId;
    Long bankTariffId;
    Set<PaymentType> paymentTypes;
}
