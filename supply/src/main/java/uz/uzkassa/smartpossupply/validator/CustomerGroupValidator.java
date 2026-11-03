package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.customergroup.CustomerGroupDTO;
import uz.uzkassa.smartpossupply.repository.CustomerGroupRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerGroupValidator extends BaseValidator implements ValidatorStrategy<CustomerGroupDTO> {

    CustomerGroupRepository customerGroupRepository;

    public CustomerGroupValidator(LocalizationMessage localizationMessage, CustomerGroupRepository customerGroupRepository) {
        super(localizationMessage);
        this.customerGroupRepository = customerGroupRepository;
    }

    @Override
    public void validate(CustomerGroupDTO dto) {

        if (dto.getId() == null) {
            validateExistsInDatabase(customerGroupRepository.existsByNameAndCompanyId(dto.getName(), SecurityUtils.getCompanyId()), CATEGORY, NAME, dto.getName());
        } else {
            validateExistsInDatabase(customerGroupRepository.existsByNameAndCompanyIdAndIdNot(dto.getName(), SecurityUtils.getCompanyId(), dto.getId()), CATEGORY, NAME, dto.getName());

        }
    }
}
