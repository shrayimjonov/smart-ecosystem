package uz.uzkassa.smartpos.dto.sales;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.sales.TransactionInfo;
import uz.uzkassa.smartpos.enums.TransactionStatus;

@Getter
@Setter
public class TransactionUpdatePaymentDto {
    @NotBlank
    private String uid;
    @NotNull
    private TransactionStatus status;
    private TransactionInfo info;
    private String content;
}
