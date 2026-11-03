package uz.uzkassa.cts.dto.kapitallegal.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class KapitalLegalSendPaymentRequest implements Serializable {
    private Long client_id;
    private String sid;
    private KapitalLegalPaymentRequest payment;
}
