package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.department.DepartmentDTO;

@Component
public class DepartmentValidator extends BaseValidator implements ValidatorStrategy<DepartmentDTO> {

    public DepartmentValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(DepartmentDTO dto) {
        validateRequiredField(dto.getBranchId(), BRANCH);
        validateRequiredField(dto.getNameUzLatin(), NAME_UZ_LATIN);
        validateRequiredField(dto.getNameUzCyrillic(), NAME_UZ_CYRILLIC);
        validateRequiredField(dto.getNameRu(), NAME_RU);
    }
}
