package uz.uzkassa.smartpos.dto.sales;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.TransactionOperation;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionCreatePaymentDto {
    private String uid;
    private String originUid;
    @NotNull
    private Long branchId;
    @NotNull
    private BigDecimal amount;
    private String currency = "UZS";
    @NotNull
    private PaymentType paymentType;
    @NotNull
    private TransactionOperation operation;
}
