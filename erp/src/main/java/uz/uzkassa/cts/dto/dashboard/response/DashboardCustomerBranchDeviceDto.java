package uz.uzkassa.cts.dto.dashboard.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.DeviceStatus;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class DashboardCustomerBranchDeviceDto implements Serializable {

    private Long id;
    private Long deviceId;
    private String deviceSerialNumber;
    private Long customerBranchId;
    private String customerBranchName;
    private DeviceStatus status;
    private LocalDate dueDate;
    private LocalDate startDate;
    private LocalDate initialDate;
    private boolean freeTrailPeriod;
    private boolean serviceAllowed;
    private Long tariffId;
    private String tariffName;

}
