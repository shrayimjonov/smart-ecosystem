package uz.uzkassa.smartpossupply.dto.integration.agreement;

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
