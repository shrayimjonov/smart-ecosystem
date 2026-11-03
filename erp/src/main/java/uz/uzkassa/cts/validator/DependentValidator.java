package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.dependent.DependentDTO;

@Component
public class DependentValidator extends BaseValidator implements ValidatorStrategy<DependentDTO> {

    public DependentValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(DependentDTO dto) {
        validateRequiredField(dto.getFirstName(), FIRST_NAME);
        validateRequiredField(dto.getLastName(), LAST_NAME);
        validateRequiredField(dto.getPatronymic(), PATRONYMIC);
        validateRequiredField(dto.getEmployeeId(), EMPLOYEE);
        validateRequiredField(dto.getRelationshipId(), RELATIONSHIP);
        validateRequiredField(dto.getGender(), GENDER);
    }
}
