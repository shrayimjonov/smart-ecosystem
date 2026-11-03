package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.vacancyapplication.VacancyApplicationDTO;

@Component
public class VacancyApplicationValidator extends BaseValidator implements ValidatorStrategy<VacancyApplicationDTO> {

    public VacancyApplicationValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(VacancyApplicationDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        validateRequiredField(dto.getCompanyId(), COMPANY);
        validateRequiredField(dto.getBranchId(), BRANCH);
        validateRequiredField(dto.getDepartmentId(), DEPARTMENT);
        validateRequiredField(dto.getPositionId(), POSITION);
        validateRequiredField(dto.getApproverId(), APPROVER);
    }
}
