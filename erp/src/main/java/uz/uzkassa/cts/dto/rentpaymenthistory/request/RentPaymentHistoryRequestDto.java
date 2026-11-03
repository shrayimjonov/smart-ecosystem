package uz.uzkassa.cts.dto.rentpaymenthistory.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

@Getter
@Setter
public class RentPaymentHistoryRequestDto {
    private Long customerId;
    private Long offerId;
    private String customerName;
    private String deviceSerialNumber;
    private Month month;
    private Integer year;
    private LocalDate dueDate;
    private LocalDate startDate;
    private BigDecimal amount;
}
