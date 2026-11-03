package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.stockin.StockInDTO;

@Component
public class StockInValidator extends BaseValidator implements ValidatorStrategy<StockInDTO> {

    public StockInValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(StockInDTO dto) {
        validateRequiredField(dto.getCompanyId(), COMPANY);
        validateRequiredField(dto.getBranchId(), BRANCH);
        validateRequiredField(dto.getWarehouseId(), WAREHOUSE);
        validateRequiredField(dto.getStockDate(), DATE);
        validateRequiredField(dto.getItems(), ITEMS);
    }

}
