package uz.uzkassa.cts.dto.customerbranchdevicechangetariff;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.DevicePaymentType;

@Getter
@Setter
public class CustomerBranchDeviceChangePaymentTypeDto {
    @NotNull
    private Long customerBranchDeviceId;
    @NotNull
    private DevicePaymentType paymentTypeEnum;
}
