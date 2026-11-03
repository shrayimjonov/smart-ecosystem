package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stockadjustment.StockAdjustmentDTO;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class StockAdjustmentValidator extends BaseValidator implements ValidatorStrategy<StockAdjustmentDTO> {

    public StockAdjustmentValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(StockAdjustmentDTO dto) {
        validateRequiredField(dto.getBranchId(), BRANCH);
        validateRequiredField(dto.getWarehouseId(), WAREHOUSE);
        validateRequiredField(dto.getProducts(), PRODUCTS);
    }
}
