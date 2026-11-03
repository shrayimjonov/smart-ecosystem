package uz.uzkassa.cts.dto.integration.billing;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.billing.AgreementStatus;

import java.io.Serializable;

@Getter
@Setter
public class AgreementResponseDTO implements Serializable {
    private Long id;

    private AgreementStatus status;
}

