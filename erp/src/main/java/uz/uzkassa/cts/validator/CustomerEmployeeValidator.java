package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.customeremployee.CustomerEmployeeDTO;

@Component
public class CustomerEmployeeValidator extends BaseValidator implements ValidatorStrategy<CustomerEmployeeDTO> {

    public CustomerEmployeeValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(CustomerEmployeeDTO dto) {
        validateRequiredField(dto.getCustomerId(), CUSTOMER);
        validateRequiredField(dto.getRole(), ROLES);
        validateRequiredField(dto.getFirstName(), FIRST_NAME);
        validateRequiredField(dto.getLastName(), LAST_NAME);
    }
}
