package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.product.ProductDTO;

@Component
public class ProductValidator extends BaseValidator implements ValidatorStrategy<ProductDTO> {

    public ProductValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(ProductDTO dto) {
        validateRequiredField(dto.getNameUzLatin(), NAME_UZ_LATIN);
        validateRequiredField(dto.getNameUzCyrillic(), NAME_UZ_CYRILLIC);
        validateRequiredField(dto.getNameRu(), NAME_RU);
        validateRequiredField(dto.getPrice(), PRICE);
        validateRequiredField(dto.getCategoryId(), CATEGORY);
    }
}
