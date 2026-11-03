package uz.uzkassa.smartpos.integration.cto;

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
}
