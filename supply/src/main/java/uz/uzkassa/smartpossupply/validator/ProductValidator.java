package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.product.ProductDTO;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ProductValidator extends BaseValidator implements ValidatorStrategy<ProductDTO> {

    public ProductValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(ProductDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        validateRequiredField(dto.getBranchId(), BRANCH);
        validateRequiredField(dto.getVat(), VAT);
    }
}
