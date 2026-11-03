package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.dto.pricelist.PriceListDTO;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class PriceListValidator extends BaseValidator implements ValidatorStrategy<PriceListDTO> {

    public PriceListValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(PriceListDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        validateRequiredField(dto.getBranchId(), BRANCH);
        validateRequiredField(dto.getItems(), ITEMS);

        if (!dto.isApplyToAllCustomers()) {
            validateRequiredField(dto.getCustomers(), CUSTOMER);
        }
    }
}
