package uz.uzkassa.cts.dto.servicepaymenthistory;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.DeviceSuspendStatus;
import uz.uzkassa.cts.enums.FreeTrialPeriodType;
import uz.uzkassa.cts.enums.PeriodType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServicePaymentHistoryCronDto implements Serializable {
    private Long customerDeviceBranchId;
    private boolean deleted;
    private Long customerBranchId;
    private Long deviceId;
    @Enumerated(EnumType.STRING)
    private DeviceStatus status;
    private LocalDate dueDate;
    private boolean freeTrailPeriod;
    private LocalDate startDate;
    private LocalDate initialDate;
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
    private Long servicePaymentHistoryId;
    private Month servicePaymentHistoryMonth;
    private Integer servicePaymentHistoryYear;
    @Enumerated(EnumType.STRING)
    private DeviceSuspendStatus suspendStatus;
    private LocalDate suspendDate;
    private LocalDate fiscalizationCreatedDate;
}
