package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.category.CategoryDTO;

@Component
public class CategoryValidator extends BaseValidator implements ValidatorStrategy<CategoryDTO> {

    public CategoryValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(CategoryDTO dto) {
        validateRequiredField(dto.getNameUzLatin(), NAME_UZ_LATIN);
        validateRequiredField(dto.getNameUzCyrillic(), NAME_UZ_CYRILLIC);
        validateRequiredField(dto.getNameUzCyrillic(), NAME_RU);
        validateRequiredField(dto.getCompanyId(), COMPANY);
    }
}
