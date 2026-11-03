package uz.uzkassa.cts.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.candidate.CandidateDTO;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CandidateValidator extends BaseValidator implements ValidatorStrategy<CandidateDTO> {

    public CandidateValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(CandidateDTO dto) {
        validateRequiredField(dto.getFirstName(), FIRST_NAME);
        validateRequiredField(dto.getLastName(), LAST_NAME);
        validateRequiredField(dto.getPatronymic(), PATRONYMIC);
        validateRequiredField(dto.getPositionId(), POSITION);
        validateRequiredField(dto.getDepartmentId(), DEPARTMENT);
        validateRequiredField(dto.getVacancyId(), VACANCY);
        validateRequiredField(dto.getPhones(), PHONES);
    }
}
