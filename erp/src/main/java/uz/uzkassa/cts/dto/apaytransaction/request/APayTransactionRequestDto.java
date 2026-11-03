package uz.uzkassa.cts.dto.apaytransaction.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class APayTransactionRequestDto {
    private String transactionId;
    private Long amount;
    private String tin;
    private String pan;
}
