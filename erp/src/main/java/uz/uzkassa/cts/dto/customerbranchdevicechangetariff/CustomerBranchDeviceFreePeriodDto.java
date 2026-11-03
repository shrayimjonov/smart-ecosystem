package uz.uzkassa.cts.dto.customerbranchdevicechangetariff;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerBranchDeviceFreePeriodDto {
    @NotNull
    private Long customerBranchDeviceId;
    @NotNull
    private LocalDate freePeriodDate;
}
