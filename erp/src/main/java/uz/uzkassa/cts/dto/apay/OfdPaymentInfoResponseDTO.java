package uz.uzkassa.cts.dto.apay;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class OfdPaymentInfoResponseDTO implements Serializable {
    int code;
    String message; // содержит детальную информацию об ошибке для разработчика
    String terminalId; //содержит номер ВФМ
    Long receiptId; //содержит уникальный порядковый номер платежа для ВФМ
    String fiscalSign; //содержит ФП чека платежа
    LocalDateTime dateTime; //содержит ФП чека платежа
    String qrCodeUrl; //содержит ФП чека платежа
}
