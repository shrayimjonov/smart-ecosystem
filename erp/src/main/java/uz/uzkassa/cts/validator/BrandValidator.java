package uz.uzkassa.cts.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.repository.BrandRepository;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandValidator extends BaseValidator implements ValidatorStrategy<CommonDTO> {

    BrandRepository brandRepository;

    public BrandValidator(LocalizationMessage localizationMessage, BrandRepository brandRepository) {
        super(localizationMessage);
        this.brandRepository = brandRepository;
    }

    @Override
    public void validate(CommonDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        if (dto.getId() == null) {
            validateExistsInDatabase(brandRepository.existsByNameIsIgnoreCase(dto.getName()), BRAND, NAME, dto.getName());
        }
        if (dto.getId() != null) {
            validateExistsInDatabase(brandRepository.existsByNameIsIgnoreCaseAndIdNot(dto.getName(), dto.getId()), BRAND, NAME, dto.getName());
        }
    }
}
