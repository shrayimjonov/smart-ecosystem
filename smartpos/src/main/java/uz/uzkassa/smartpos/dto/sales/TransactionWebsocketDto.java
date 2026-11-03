package uz.uzkassa.smartpos.dto.sales;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.sales.TransactionInfo;
import uz.uzkassa.smartpos.enums.TransactionStatus;

@Getter
@Setter
public class TransactionWebsocketDto {
    private TransactionStatus status;
    private TransactionInfo info;
    private String content;
}
