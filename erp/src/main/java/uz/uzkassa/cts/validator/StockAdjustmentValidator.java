package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.stockadjustment.StockAdjustmentDTO;

@Component
public class StockAdjustmentValidator extends BaseValidator implements ValidatorStrategy<StockAdjustmentDTO> {

    public StockAdjustmentValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(StockAdjustmentDTO dto) {
        validateRequiredField(dto.getAdjustmentDate(), DATE);
        validateRequiredField(dto.getCompanyId(), COMPANY);
        validateRequiredField(dto.getBranchId(), BRANCH);
        validateRequiredField(dto.getWarehouseId(), WAREHOUSE);
    }

}
