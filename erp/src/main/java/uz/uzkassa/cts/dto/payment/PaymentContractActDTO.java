package uz.uzkassa.cts.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentContractActDTO {

    @NotNull
    private Long paymentContractId;
    @NotNull
    private Long deviceId;

    public PaymentContractActDTO(Long paymentContractId, Long deviceId) {
        this.paymentContractId = paymentContractId;
        this.deviceId = deviceId;
    }
}
