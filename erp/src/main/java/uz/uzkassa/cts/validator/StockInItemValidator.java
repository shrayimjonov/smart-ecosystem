package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.stockin.StockInItemDTO;

@Component
public class StockInItemValidator extends BaseValidator implements ValidatorStrategy<StockInItemDTO> {

    public StockInItemValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(StockInItemDTO dto) {
        validateRequiredField(dto.getItemId(), PRODUCT);
        validateRequiredField(dto.getUnitId(), UNITS);
        validateRequiredField(dto.getQty(), QTY);
    }
}
