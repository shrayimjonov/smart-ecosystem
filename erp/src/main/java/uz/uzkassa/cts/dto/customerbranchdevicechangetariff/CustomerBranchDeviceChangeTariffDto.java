package uz.uzkassa.cts.dto.customerbranchdevicechangetariff;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CustomerBranchDeviceChangeTariffDto {
    @NotNull
    private Long customerBranchDeviceId;
}
