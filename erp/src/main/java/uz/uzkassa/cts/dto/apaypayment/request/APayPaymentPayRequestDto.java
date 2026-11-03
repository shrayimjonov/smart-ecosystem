package uz.uzkassa.cts.dto.apaypayment.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class APayPaymentPayRequestDto implements Serializable {
    private String billId;
    private String confirmationKey;
    private Integer transactionId;

    @Override
    public String toString() {
        return "APayPaymentPayRequestDto{" +
            "billId='" + billId + '\'' +
            ", confirmationKey='" + confirmationKey + '\'' +
            ", transactionId=" + transactionId +
            '}';
    }

    public APayPaymentPayRequestDto() {
    }

    public APayPaymentPayRequestDto(String billId, String confirmationKey, Integer transactionId) {
        this.billId = billId;
        this.confirmationKey = confirmationKey;
        this.transactionId = transactionId;
    }
}
