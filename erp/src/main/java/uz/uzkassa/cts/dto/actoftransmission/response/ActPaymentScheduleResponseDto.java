package uz.uzkassa.cts.dto.actoftransmission.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.apay.APayStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ActPaymentScheduleResponseDto {
    private Long id;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private BigDecimal balance;
    private Integer totalProductCount;
    private ContractStatus status;
    private APayStatus paymentStatus;
}
