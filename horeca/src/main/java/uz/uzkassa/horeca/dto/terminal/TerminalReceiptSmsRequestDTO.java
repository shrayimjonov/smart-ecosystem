package uz.uzkassa.horeca.dto.terminal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TerminalReceiptSmsRequestDTO {
    private String phone;
    private Long receiptId;
    private String uid;
    private String fiscalUrl;

    String language;
}
