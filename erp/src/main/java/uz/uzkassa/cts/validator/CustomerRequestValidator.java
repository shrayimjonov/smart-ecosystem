package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.customerRequest.CustomerRequestDTO;

@Component
public class CustomerRequestValidator extends BaseValidator implements ValidatorStrategy<CustomerRequestDTO> {

    public CustomerRequestValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(CustomerRequestDTO dto) {
        validateRequiredField(dto.getRequestType(), REQUEST_TYPE);
        validateRequiredField(dto.getCustomerId(), CUSTOMER);
    }
}
