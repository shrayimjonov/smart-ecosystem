package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.company.CompanyDTO;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CompanyValidator extends BaseValidator implements ValidatorStrategy<CompanyDTO> {

    public CompanyValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(CompanyDTO dto) {
        validateRequiredField(dto.getName(), NAME);
    }
}
