package uz.uzkassa.cts.dto.freeperiodhistory.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class FreePeriodHistoryResponseDto {
    private Long id;
    private Long creatorId;
    private String creatorName;
    private Long lastModifierId;
    private String lastModifierName;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private LocalDate freePeriodDate;
    private String customerName;
    private Long customerId;
    private String tin;
    private String deviceSerialNumber;
    private Long customerBranchDeviceId;
}
