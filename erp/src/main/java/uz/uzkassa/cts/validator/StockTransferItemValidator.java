package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.stocktransfer.StockTransferItemDTO;

@Component
public class StockTransferItemValidator extends BaseValidator implements ValidatorStrategy<StockTransferItemDTO> {

    public StockTransferItemValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(StockTransferItemDTO dto) {
        validateRequiredField(dto.getItemId(), PRODUCT);
        validateRequiredField(dto.getUnitId(), UNITS);
        validateRequiredField(dto.getQty(), QTY);
    }
}
