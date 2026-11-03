package uz.uzkassa.smartpos.integration.apay.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefundInfoDTO {
    String terminalId; //содержит номер ВФМ
    String receiptId; //содержит уникальный порядковый номер платежа для ВФМ
    String fiscalSign; //содержит ФП чека платежа
    String dateTime; //содержит ФП чека платежа
}
