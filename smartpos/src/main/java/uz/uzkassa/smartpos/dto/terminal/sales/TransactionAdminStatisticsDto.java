package uz.uzkassa.smartpos.dto.terminal.sales;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.TransactionOperation;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionAdminStatisticsDto {
    private PaymentType paymentType;
    private TransactionOperation operation;
    private BigDecimal totalAmount;
    private Long count;
}
