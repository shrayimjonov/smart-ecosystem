package uz.uzkassa.smartpossupply.dto.integration.agreement;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.AgreementStatus;

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
