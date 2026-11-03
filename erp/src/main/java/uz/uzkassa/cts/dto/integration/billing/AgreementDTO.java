package uz.uzkassa.cts.dto.integration.billing;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.billing.AgreementStatus;

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
