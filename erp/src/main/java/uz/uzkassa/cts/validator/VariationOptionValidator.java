package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.variationoption.VariationOptionDTO;

@Component
public class VariationOptionValidator extends BaseValidator implements ValidatorStrategy<VariationOptionDTO> {

    public VariationOptionValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(VariationOptionDTO dto) {
        validateRequiredField(dto.getNameUzLatin(), NAME_UZ_LATIN);
        validateRequiredField(dto.getNameUzCyrillic(), NAME_UZ_CYRILLIC);
        validateRequiredField(dto.getNameRu(), NAME_RU);
        validateRequiredField(dto.getVariationTypeId(), VARIATION_TYPE);
    }
}
