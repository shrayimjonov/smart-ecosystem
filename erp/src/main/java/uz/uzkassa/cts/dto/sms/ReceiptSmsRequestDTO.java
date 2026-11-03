package uz.uzkassa.cts.dto.sms;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptSmsRequestDTO {
    private String phone;
    @Email
    private String email;
    private Long receiptId;
    private String uid;

    private String language;

    private String fiscalUrl;
}
