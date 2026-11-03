package uz.uzkassa.cts.dto.customerbranchdevicechangetariff;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.DeviceSuspendStatus;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CustomerBranchDeviceListDto {
    private Long customerId;
    private String tin;
    private Long branchId;
    private String branchName;
    private String customerName;
    private String deviceSerialNumber;
    private Long customerBranchDeviceId;
    private DeviceStatus status;
    private Long tariffId;
    private String tariffName;
    private boolean serviceAllowed;
    private boolean freeTrailPeriod;
    private LocalDate dueDate;
    private LocalDate startDate;
    private DeviceSuspendStatus suspendStatus;
    private LocalDate suspendDate;
    private LocalDate freePeriodDate;
}
