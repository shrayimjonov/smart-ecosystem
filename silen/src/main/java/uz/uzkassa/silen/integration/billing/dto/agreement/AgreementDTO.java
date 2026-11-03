package uz.uzkassa.silen.integration.billing.dto.agreement;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AgreementDTO  implements Serializable {
    private Integer availability;
    private AgreementStatus status;
    private String agreementNumber;
    private String operatorId;
    private String operatorName;
}
