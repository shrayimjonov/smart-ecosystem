package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.stocktransfer.StockTransferDTO;

@Component
public class StockTransferValidator extends BaseValidator implements ValidatorStrategy<StockTransferDTO> {

    public StockTransferValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(StockTransferDTO dto) {
        validateRequiredField(dto.getTransferDate(), TRANSFER_DATE);
        validateRequiredField(dto.getFromBranchId(), BRANCH);
        validateRequiredField(dto.getToBranchId(), BRANCH);
        validateRequiredField(dto.getFromWarehouseId(), WAREHOUSE);
        validateRequiredField(dto.getToWarehouseId(), WAREHOUSE);
        validateRequiredField(dto.getCompanyId(), COMPANY);
        validateRequiredField(dto.getItems(), ITEMS);
    }
}
