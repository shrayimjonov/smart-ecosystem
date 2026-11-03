package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.reference.ReferenceDTO;

@Component
public class ReferenceValidator extends BaseValidator implements ValidatorStrategy<ReferenceDTO> {

    public ReferenceValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(ReferenceDTO dto) {
        validateRequiredField(dto.getType(), TYPE);
        validateRequiredField(dto.getNameUzLatin(), NAME_UZ_LATIN);
        validateRequiredField(dto.getNameUzCyrillic(), NAME_UZ_CYRILLIC);
        validateRequiredField(dto.getNameRu(), NAME_RU);
        validateRequiredField(dto.getCode(), CODE);
    }
}
