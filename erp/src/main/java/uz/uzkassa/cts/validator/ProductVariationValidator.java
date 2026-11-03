package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.productvariation.ProductVariationDTO;

@Component
public class ProductVariationValidator extends BaseValidator implements ValidatorStrategy<ProductVariationDTO> {

    public ProductVariationValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(ProductVariationDTO dto) {
        validateRequiredField(dto.getProductId(), PRODUCT);
        validateRequiredField(dto.getVariationOptionIds(), VARIATION_OPTION);
    }
}
