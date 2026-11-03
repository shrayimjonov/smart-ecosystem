package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutDTO;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class StockOutValidator extends BaseValidator implements ValidatorStrategy<StockOutDTO> {

    public StockOutValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(StockOutDTO dto) {
        validateRequiredField(dto.getWarehouseId(), WAREHOUSE);
        validateRequiredField(dto.getBranchId(), BRANCH);
        validateRequiredField(dto.getProducts(), PRODUCTS);
    }
}
