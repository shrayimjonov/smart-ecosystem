package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.customer.CustomerDTO;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerValidator extends BaseValidator implements ValidatorStrategy<CustomerDTO> {

    public CustomerValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(CustomerDTO dto) {
        validateRequiredField(dto.getName(), NAME);
    }
}
