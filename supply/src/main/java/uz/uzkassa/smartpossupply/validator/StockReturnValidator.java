package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stockreturn.StockReturnDTO;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class StockReturnValidator extends BaseValidator implements ValidatorStrategy<StockReturnDTO> {

    public StockReturnValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(StockReturnDTO dto) {
        validateRequiredField(dto.getWarehouseId(), WAREHOUSE);
        validateRequiredField(dto.getBranchId(), BRANCH);
        validateRequiredField(dto.getProducts(), PRODUCTS);
    }
}
