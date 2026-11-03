package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.employee.marketplace.MarketplaceEmployeeDTO;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class MarketplaceEmployeeValidator extends BaseValidator implements ValidatorStrategy<MarketplaceEmployeeDTO> {

    public MarketplaceEmployeeValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(MarketplaceEmployeeDTO dto) {
        validateRequiredField(dto.getCompanyId(), COMPANY);
        validateRequiredField(dto.getPassword(), PASSWORD);
        validateRequiredField(dto.getLogin(), LOGIN);
        validateRequiredField(dto.getFirstName(), FIRST_NAME);
        validateRequiredField(dto.getLastName(), LAST_NAME);
    }
}
