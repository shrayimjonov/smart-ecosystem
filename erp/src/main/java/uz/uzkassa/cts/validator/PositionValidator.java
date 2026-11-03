package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.position.PositionDTO;

@Component
public class PositionValidator extends BaseValidator implements ValidatorStrategy<PositionDTO> {

    public PositionValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(PositionDTO dto) {
        validateRequiredField(dto.getCompanyId(), COMPANY);
        validateRequiredField(dto.getNameUzLatin(), NAME_UZ_LATIN);
        validateRequiredField(dto.getNameUzCyrillic(), NAME_UZ_CYRILLIC);
        validateRequiredField(dto.getNameRu(), NAME_RU);
    }
}
