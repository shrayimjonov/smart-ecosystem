package uz.uzkassa.cts.dto.apay;

import lombok.Data;

import java.io.Serializable;

@Data
public class APayPayRequestDto implements Serializable {
    private String billId;
    private String confirmationKey;
    private String cardId;
    private String epsToken;

    public APayPayRequestDto(String billId, String confirmationKey) {
        this.billId = billId;
        this.confirmationKey = confirmationKey;
    }
}
