package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.stockadjustment.StockAdjustmentItemDTO;

@Component
public class StockAdjustmentItemValidator extends BaseValidator implements ValidatorStrategy<StockAdjustmentItemDTO> {

    public StockAdjustmentItemValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(StockAdjustmentItemDTO dto) {
        validateRequiredField(dto.getItemId(), PRODUCT);
        validateRequiredField(dto.getUnitId(), UNITS);
        validateRequiredField(dto.getQty(), QTY);
    }
}
