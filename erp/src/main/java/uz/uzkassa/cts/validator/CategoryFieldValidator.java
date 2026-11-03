package uz.uzkassa.cts.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.categoryfield.CategoryFieldBatchDTO;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryFieldValidator extends BaseValidator implements ValidatorStrategy<CategoryFieldBatchDTO> {

    public CategoryFieldValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(CategoryFieldBatchDTO dto) {
        validateRequiredField(dto.getCategoryId(), CATEGORY);
        validateRequiredField(dto.getFieldIds(), FIELDS);
    }
}
