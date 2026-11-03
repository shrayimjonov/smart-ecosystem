package uz.uzkassa.smartpos.integration.apay.request;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class APayPaymentPayRequestDto implements Serializable {
    private String billId;
    private String confirmationKey;
    private String transactionId;
}
