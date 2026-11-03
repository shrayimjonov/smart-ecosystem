package uz.uzkassa.smartpos.dto.sales;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.TransactionOperation;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionRailwaysPaymentDto {
    @NotNull
    private String uid;
    private String originUid;
    @NotNull
    private String terminalId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private PaymentType paymentType;
    @NotNull
    private TransactionOperation operation;
}
