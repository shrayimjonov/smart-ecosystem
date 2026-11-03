package uz.uzkassa.cts.dto.kapitallegal.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class KapitalLegalCheckPaymentRequest implements Serializable {
    private Long client_id;
    private String sid;
    private List<String> uniqs;
}
