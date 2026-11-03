package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferDTO;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class StockTransferValidator extends BaseValidator implements ValidatorStrategy<StockTransferDTO> {

    public StockTransferValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(StockTransferDTO dto) {
        validateRequiredField(dto.getFromWarehouseId(), WAREHOUSE);
        validateRequiredField(dto.getToWarehouseId(), WAREHOUSE);
        validateRequiredField(dto.getFromBranchId(), BRANCH);
        validateRequiredField(dto.getToBranchId(), BRANCH);
        validateRequiredField(dto.getProducts(), PRODUCTS);
    }
}
