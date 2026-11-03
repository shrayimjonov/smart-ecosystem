package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchDTO;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerBranchValidator extends BaseValidator implements ValidatorStrategy<CustomerBranchDTO> {

    public CustomerBranchValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(CustomerBranchDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        validateRequiredField(dto.getCustomerId(), CUSTOMER);
    }
}
