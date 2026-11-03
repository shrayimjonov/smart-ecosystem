package uz.uzkassa.smartpossupply.dto.receipt;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TerminalReceiptFiscalDTO implements Serializable {

    String receiptSeq;

    @Size(max = 255)
    String fiscalSign;

    @Size(max = 255)
    String fiscalUrl;

    LocalDateTime fiscalReceiptCreatedDate;

    String terminalSerialNumber;

    String fiscalSerialNumber;

    Long shiftNumber;


    public TerminalReceiptFiscalDTO(String fiscalUrl) {
        this.fiscalUrl = fiscalUrl;
    }
}
