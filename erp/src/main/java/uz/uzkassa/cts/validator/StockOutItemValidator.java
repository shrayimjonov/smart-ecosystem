package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.stockout.StockOutItemDTO;

@Component
public class StockOutItemValidator extends BaseValidator implements ValidatorStrategy<StockOutItemDTO> {

    public StockOutItemValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(StockOutItemDTO dto) {
        validateRequiredField(dto.getItemId(), PRODUCT);
        validateRequiredField(dto.getUnitId(), UNITS);
        validateRequiredField(dto.getQty(), QTY);
    }
}
