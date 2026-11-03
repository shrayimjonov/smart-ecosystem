package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.vacancy.VacancyDTO;

@Component
public class VacancyValidator extends BaseValidator implements ValidatorStrategy<VacancyDTO> {

    public VacancyValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(VacancyDTO dto) {
        validateRequiredField(dto.getCompanyId(), COMPANY);
        validateRequiredField(dto.getBranchId(), BRANCH);
        validateRequiredField(dto.getDepartmentId(), DEPARTMENT);
        validateRequiredField(dto.getPositionId(), POSITION);
        validateRequiredField(dto.getName(), NAME);
    }
}
