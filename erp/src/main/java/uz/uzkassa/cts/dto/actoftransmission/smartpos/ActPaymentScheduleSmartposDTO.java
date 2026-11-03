package uz.uzkassa.cts.dto.actoftransmission.smartpos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ActPaymentScheduleSmartposDTO implements Serializable {
    private LocalDate paymentDate;
    private BigDecimal amount;
    private BigDecimal balance;
    private boolean paid = false;
}
