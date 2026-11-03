package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.stockout.StockOutDTO;

@Component
public class StockOutValidator extends BaseValidator implements ValidatorStrategy<StockOutDTO> {

    public StockOutValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(StockOutDTO dto) {
        validateRequiredField(dto.getCompanyId(), COMPANY);
        validateRequiredField(dto.getBranchId(), BRANCH);
        validateRequiredField(dto.getWarehouseId(), WAREHOUSE);
        validateRequiredField(dto.getStockDate(), DATE);
        validateRequiredField(dto.getItems(), ITEMS);
    }
}
