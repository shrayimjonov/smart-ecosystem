package uz.uzkassa.cts.dto.integration.billing;

import lombok.Data;

import java.io.Serializable;

@Data
public class APayPaymentPayRequestDto  implements Serializable {
    private String billId;
    private String confirmationKey;
    private String transactionId;

    public APayPaymentPayRequestDto() {
    }

    public APayPaymentPayRequestDto(String billId, String confirmationKey, String transactionId) {
        this.billId = billId;
        this.confirmationKey = confirmationKey;
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "APayPaymentPayRequestDto{" +
            "billId='" + billId + '\'' +
            ", confirmationKey='" + confirmationKey + '\'' +
            ", transactionId=" + transactionId +
            '}';
    }
}
