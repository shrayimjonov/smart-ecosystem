package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.customerbranchapplication.base.CustomerBranchApplicationDTO;

@Component
public class CustomerBranchApplicationValidator extends BaseValidator implements ValidatorStrategy<CustomerBranchApplicationDTO> {

    public CustomerBranchApplicationValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(CustomerBranchApplicationDTO dto) {
        validateRequiredField(dto.getCustomerBranchId(), BRANCH);
        validateRequiredField(dto.getFields(), FIELDS);
    }
}
