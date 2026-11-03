package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.vacancyapplicationtemplate.VacancyApplicationTemplateDTO;

@Component
public class VacancyApplicationTemplateValidator extends BaseValidator implements ValidatorStrategy<VacancyApplicationTemplateDTO> {

    public VacancyApplicationTemplateValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(VacancyApplicationTemplateDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        validateRequiredField(dto.getCompanyId(), COMPANY);
        validateRequiredField(dto.getPositionId(), POSITION);
        validateRequiredField(dto.getResponsibility(), RESPONSIBILITY);
        validateRequiredField(dto.getCondition(), CONDITION);
        validateRequiredField(dto.getRequirement(), REQUIREMENT);
    }
}
