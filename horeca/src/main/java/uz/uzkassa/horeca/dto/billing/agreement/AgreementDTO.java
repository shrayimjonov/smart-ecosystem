package uz.uzkassa.horeca.dto.billing.agreement;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.enums.billing.AgreementStatus;

import java.io.Serializable;

@Getter
@Setter
public class AgreementDTO implements Serializable {

    private Integer availability;
    private AgreementStatus status;
    private String agreementNumber;
    private String operatorId;
    private String operatorName;
}
