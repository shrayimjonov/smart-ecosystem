package uz.uzkassa.cts.dto.kapitallegal.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalSendPaymentStatus;

import java.io.Serializable;

@Getter
@Setter
public class KapitalLegalSendPaymentResponse implements Serializable {
    private KapitalLegalError error;
    private Long id;
    private KapitalLegalSendPaymentStatus result;
}
