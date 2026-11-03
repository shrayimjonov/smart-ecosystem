package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.customerbank.CustomBankDTO;
import uz.uzkassa.cts.repository.CustomerBankRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerBankValidator extends BaseValidator implements ValidatorStrategy<CustomBankDTO> {

    CustomerBankRepository customerBankRepository;

    public CustomerBankValidator(LocalizationMessage localizationMessage, CustomerBankRepository customerBankRepository) {
        super(localizationMessage);
        this.customerBankRepository = customerBankRepository;
    }

    @Override
    public void validate(CustomBankDTO dto) {
        validateRequiredField(dto.getBankId(), BANK);
        validateRequiredField(dto.getCustomerId(), CUSTOMER);
        validateRequiredField(dto.getAccountNumber(), ACCOUNT_NUMBER);

        if (dto.getId() == null) {
            validateExistsInDatabase(customerBankRepository.existsByBankIdAndCustomerIdAndAccountNumberAndDeletedIsFalse(dto.getBankId(), dto.getCustomerId(), dto.getAccountNumber()), BANK, ACCOUNT_NUMBER, dto.getAccountNumber());
        } else {
            validateExistsInDatabase(customerBankRepository.existsByBankIdAndCustomerIdAndAccountNumberAndDeletedIsFalseAndIdNot(dto.getBankId(), dto.getCustomerId(), dto.getAccountNumber(), dto.getId()), BANK, ACCOUNT_NUMBER, dto.getAccountNumber());
        }
    }
}
