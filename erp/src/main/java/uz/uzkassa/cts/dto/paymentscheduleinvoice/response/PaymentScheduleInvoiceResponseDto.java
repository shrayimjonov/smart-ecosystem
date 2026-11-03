package uz.uzkassa.cts.dto.paymentscheduleinvoice.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ContractStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PaymentScheduleInvoiceResponseDto {
    private Long id;
    private LocalDateTime createdDate;
    private String serviceName;
    private ContractStatus status;
    private Integer totalProductCount;
    private BigDecimal totalSellPrice;
    private BigDecimal totalPrice;
    private BigDecimal totalVatPrice;
    private Long contractId;
    private String number;
    private List<PaymentScheduleInvoiceDetailResponseDto> details;
}
