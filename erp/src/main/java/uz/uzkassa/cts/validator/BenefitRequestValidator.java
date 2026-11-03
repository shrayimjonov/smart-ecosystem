package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.benefitrequest.BenefitRequestDTO;

@Component
public class BenefitRequestValidator extends BaseValidator implements ValidatorStrategy<BenefitRequestDTO> {

    public BenefitRequestValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(BenefitRequestDTO dto) {
        validateRequiredField(dto.getRequestDate(), DATE);
        validateRequiredField(dto.getCompanyId(), COMPANY);
        validateRequiredField(dto.getApproverId(), APPROVER);
        validateRequiredField(dto.getTypeId(), TYPE);
        validateRequiredField(dto.getQuantity(), QTY);
    }
}
