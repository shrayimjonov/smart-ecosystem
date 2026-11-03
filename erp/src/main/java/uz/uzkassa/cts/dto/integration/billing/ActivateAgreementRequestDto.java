package uz.uzkassa.cts.dto.integration.billing;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ActivateAgreementRequestDto implements Serializable {
    private Long agreementId;
}

