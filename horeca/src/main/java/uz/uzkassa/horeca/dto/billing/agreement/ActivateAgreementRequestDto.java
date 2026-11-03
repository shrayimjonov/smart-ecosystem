package uz.uzkassa.horeca.dto.billing.agreement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivateAgreementRequestDto {

    private Long agreementId;

    public ActivateAgreementRequestDto(Long agreementId) {
        this.agreementId = agreementId;
    }
}
