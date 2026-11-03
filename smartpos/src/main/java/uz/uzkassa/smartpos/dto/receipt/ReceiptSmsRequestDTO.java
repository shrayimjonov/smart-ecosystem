package uz.uzkassa.smartpos.dto.receipt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptSmsRequestDTO {
    private String phone;
    private Long receiptId;
    private String uid;
    private String fiscalUrl;

    String language;
}
