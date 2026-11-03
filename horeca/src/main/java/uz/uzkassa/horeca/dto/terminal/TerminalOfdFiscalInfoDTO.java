package uz.uzkassa.horeca.dto.terminal;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TerminalOfdFiscalInfoDTO {
    String paymentId; //Уникальный идентификатор платежа в ЭПC. (символы A-Z,a-z,0-9,-)

    String terminalId; //Сер.№ ФМ

    Long receiptSeq; //Номер чека

    LocalDateTime dateTime; //Время регистрации чека (формат ГГГГММДДЧСМНСК)

    String fiscalSign; //содержит ФП чека платежа

    String qrCodeUrl; //URL
}
