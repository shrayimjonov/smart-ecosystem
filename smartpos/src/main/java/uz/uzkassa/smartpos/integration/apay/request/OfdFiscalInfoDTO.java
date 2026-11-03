package uz.uzkassa.smartpos.integration.apay.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OfdFiscalInfoDTO {
    String paymentId; //Уникальный идентификатор платежа в ЭПC. (символы A-Z,a-z,0-9,-)

    String terminalId; //Сер.№ ФМ

    Long receiptSeq; //Номер чека

    LocalDateTime dateTime; //Время регистрации чека (формат ГГГГММДДЧСМНСК)

    String fiscalSign; //содержит ФП чека платежа

    String qrCodeUrl; //URL
}
