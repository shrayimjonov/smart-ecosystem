package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.variationtype.VariationTypeDTO;

@Component
public class VariationTypeValidator extends BaseValidator implements ValidatorStrategy<VariationTypeDTO> {

    public VariationTypeValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(VariationTypeDTO dto) {
        validateRequiredField(dto.getNameUzLatin(), NAME_UZ_LATIN);
        validateRequiredField(dto.getNameUzCyrillic(), NAME_UZ_CYRILLIC);
        validateRequiredField(dto.getNameRu(), NAME_RU);
        validateRequiredField(dto.getWidgetType(), WIDGET_TYPE);
    }
}
