package uz.uzkassa.smartpos.dto.terminal.sales;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionDynamicsDto {
    private LocalDateTime dateTime;
    private BigDecimal totalAmount;
}
