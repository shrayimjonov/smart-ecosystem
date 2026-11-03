package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.customerbranch.rent.CustomerBranchRentDTO;
import uz.uzkassa.cts.repository.CustomerBranchRentRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerBranchRentValidator extends BaseValidator implements ValidatorStrategy<CustomerBranchRentDTO> {

    CustomerBranchRentRepository customerBranchRentRepository;

    public CustomerBranchRentValidator(LocalizationMessage localizationMessage, CustomerBranchRentRepository customerBranchRentRepository) {
        super(localizationMessage);
        this.customerBranchRentRepository = customerBranchRentRepository;
    }

    @Override
    public void validate(CustomerBranchRentDTO dto) {
        validateRequiredField(dto.getCustomerBranchId(), BRANCH);
        validateRequiredField(dto.getRentNumber(), RENT_NUMBER);
        validateRequiredField(dto.getStartDate(), DATE);
        validateRequiredField(dto.getEndDate(), DATE);

        if (dto.getId() == null) {
            validateExistsInDatabase(customerBranchRentRepository.existsByCustomerBranchIdAndRentNumber(dto.getCustomerBranchId(), dto.getRentNumber()), RENT, RENT_NUMBER, dto.getRentNumber());
        } else {
            validateExistsInDatabase(customerBranchRentRepository.existsByCustomerBranchIdAndRentNumberAndIdNot(dto.getCustomerBranchId(), dto.getRentNumber(), dto.getId()), RENT, RENT_NUMBER, dto.getRentNumber());
        }
    }
}
