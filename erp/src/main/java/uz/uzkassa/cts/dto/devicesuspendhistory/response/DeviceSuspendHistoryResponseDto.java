package uz.uzkassa.cts.dto.devicesuspendhistory.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Month;

@Getter
@Setter
public class DeviceSuspendHistoryResponseDto {
    private Long customerBranchDeviceId;
    private Long customerBranchId;
    private Long tariffId;
    private String tariffName;
    private Long contractId;
    private String deviceSerialNumber;
    private Month month;
    private Integer year;
    private LocalDate dueDate;
    private LocalDate startDate;
    private boolean active = true;
    private String creatorFullName;
    private Long creatorId;
    private String lastModifierFullName;
    private Long lastModifierId;
    private String customerName;
    private Long customerId;
}
