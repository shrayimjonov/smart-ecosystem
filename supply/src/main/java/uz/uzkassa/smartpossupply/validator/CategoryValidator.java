package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.category.CategoryDTO;
import uz.uzkassa.smartpossupply.repository.CategoryRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CategoryValidator extends BaseValidator implements ValidatorStrategy<CategoryDTO> {

    CategoryRepository categoryRepository;

    public CategoryValidator(LocalizationMessage localizationMessage, CategoryRepository categoryRepository) {
        super(localizationMessage);
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void validate(CategoryDTO dto) {
        if (dto.getId() == null) {
            validateExistsInDatabase(categoryRepository.existsByBranchIdAndCatalogCodeAndEnvironmentTypeAndDeletedIsFalse(dto.getBranchId(), dto.getCatalogCode(), dto.getEnvironmentType()), CATEGORY, CODE, dto.getCatalogCode());
        } else {
            validateExistsInDatabase(categoryRepository.existsByBranchIdAndCatalogCodeAndEnvironmentTypeAndIdNotAndDeletedIsFalse(dto.getBranchId(), dto.getCatalogCode(), dto.getEnvironmentType(), dto.getId()), CATEGORY, CODE, dto.getCatalogCode());
        }
    }
}
