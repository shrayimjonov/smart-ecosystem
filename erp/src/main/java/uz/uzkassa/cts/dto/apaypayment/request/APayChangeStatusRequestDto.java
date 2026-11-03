package uz.uzkassa.cts.dto.apaypayment.request;

import lombok.Data;
import uz.uzkassa.cts.enums.apay.APayPaymentStatus;

import java.io.Serializable;

@Data
public class APayChangeStatusRequestDto implements Serializable {
    private Long id;
    private APayPaymentStatus status;

    @Override
    public String toString() {
        return "APayChangeStatusRequestDto{" +
            "id=" + id +
            ", status=" + status +
            '}';
    }
}
