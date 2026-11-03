package uz.uzkassa.cts.dto.cron;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.FreeTrialPeriodType;
import uz.uzkassa.cts.enums.PeriodType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CronDeviceSwitchDto implements Serializable {

    private Long customerDeviceBranchId;
    private boolean deleted;
    private Long customerBranchId;
    private Long deviceId;
    @Enumerated(EnumType.STRING)
    private DeviceStatus status;
    private LocalDate dueDate;
    private boolean freeTrailPeriod;
    private LocalDate startDate;
    private boolean serviceAllowed;
    private Long tariffId;
    private String tariffName;
    private Integer tariffPeriod;
    @Enumerated(EnumType.STRING)
    private PeriodType tariffPeriodType;
    private BigDecimal tariffPrice;
    private String tariffSpecialType;
    private Integer tariffFreeTrailPeriod;
    @Enumerated(EnumType.STRING)
    private FreeTrialPeriodType tariffFreeTrailPeriodType;
    private Long customerId;
    private String customerTin;
    private Long contractId;
    private String serialNumber;
    @Enumerated(EnumType.STRING)
    private DeviceStatus fiscalizationStatus;

}
