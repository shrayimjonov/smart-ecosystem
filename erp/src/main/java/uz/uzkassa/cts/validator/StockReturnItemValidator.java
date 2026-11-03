package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.stockreturn.StockReturnItemDTO;

@Component
public class StockReturnItemValidator extends BaseValidator implements ValidatorStrategy<StockReturnItemDTO> {

    public StockReturnItemValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(StockReturnItemDTO dto) {
        validateRequiredField(dto.getItemId(), PRODUCT);
        validateRequiredField(dto.getUnitId(), UNITS);
        validateRequiredField(dto.getQty(), QTY);
    }
}
