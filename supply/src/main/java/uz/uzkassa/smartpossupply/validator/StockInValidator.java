package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stockin.StockInDTO;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class StockInValidator extends BaseValidator implements ValidatorStrategy<StockInDTO> {

    public StockInValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(StockInDTO dto) {
        validateRequiredField(dto.getWarehouseId(), WAREHOUSE);
        validateRequiredField(dto.getBranchId(), BRANCH);
        validateRequiredField(dto.getProducts(), PRODUCTS);
    }
}
