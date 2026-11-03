package uz.uzkassa.cts.dto.servicepaymenthistory.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

@Getter
@Setter
public class ServicePaymentHistoryResponseDto implements Serializable {
    private Long id;
    private Long customerBranchDeviceId;
    private Long customerId;
    private Long contractId;
    private Long serviceOrderInvoiceId;
    private Long tariffId;
    private String tariffName;
    private String deviceSerialNumber;
    private Month month;
    private Integer year;
    private LocalDate dueDate;
    private LocalDate startDate;
    private BigDecimal amount;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
