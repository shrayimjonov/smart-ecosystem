package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.contact.ContactDTO;
import uz.uzkassa.cts.dto.customerbranch.CustomerBranchDTO;
import uz.uzkassa.cts.repository.CustomerBranchRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerBranchValidator extends BaseValidator implements ValidatorStrategy<CustomerBranchDTO> {

    CustomerBranchRepository customerBranchRepository;

    public CustomerBranchValidator(LocalizationMessage localizationMessage, CustomerBranchRepository customerBranchRepository) {
        super(localizationMessage);
        this.customerBranchRepository = customerBranchRepository;
    }

    @Override
    public void validate(CustomerBranchDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        validateRequiredField(dto.getCustomerId(), CUSTOMER);
        validateRequiredField(dto.getActivityTypeId(), TYPE);
        validateRequiredField(dto.getAddress(), ADDRESS);
        validateRequiredField(dto.getAddress().getRegionId(), REGION);
        validateRequiredField(dto.getAddress().getDistrictId(), DISTRICT);
        if (!dto.isFromQuickRegister()) {
            validateRequiredField(dto.getAddress().getNeighborhoodId(), NEIGHBORHOOD);
            validateRequiredField(dto.getPhones(), PHONES);
            String phone = dto.getPhones().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName();
            validateRequiredField(phone, PRIMARY_PHONE);
        }

        if (dto.getId() == null) {
            validateExistsInDatabase(customerBranchRepository.existsByNameIsIgnoreCaseAndCustomerIdAndDeletedIsFalse(dto.getName(), dto.getCustomerId()), BRANCH, NAME, dto.getName());
        } else {
            validateExistsInDatabase(customerBranchRepository.existsByNameIsIgnoreCaseAndCustomerIdAndDeletedIsFalseAndIdNot(dto.getName(), dto.getCustomerId(), dto.getId()), BRANCH, NAME, dto.getName());

        }
    }
}
