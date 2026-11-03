package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.contact.ContactDTO;
import uz.uzkassa.cts.dto.customer.CustomerDTO;
import uz.uzkassa.cts.repository.CustomerRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerValidator extends BaseValidator implements ValidatorStrategy<CustomerDTO> {

    CustomerRepository customerRepository;

    public CustomerValidator(LocalizationMessage localizationMessage, CustomerRepository customerRepository) {
        super(localizationMessage);
        this.customerRepository = customerRepository;
    }

    @Override
    public void validate(CustomerDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        validateRequiredField(dto.getPhones(), PHONES);
        validateRequiredField(dto.getCompanyName(), NAME);
        String phone = dto.getPhones().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName();
        validateRequiredField(phone, PRIMARY_PHONE);
        if (!dto.isFromQuickRegister()) {
            validateRequiredField(dto.getActivityTypeId(), TYPE);
            validateRequiredField(dto.getBusinessType(), TYPE);
            validateRequiredField(dto.getVatId(), VAT);
            validateRequiredField(dto.getBranches(), BRANCH);
            validateRequiredField(dto.getAddress(), ADDRESS);
            validateRequiredField(dto.getAddress().getRegionId(), REGION);
            validateRequiredField(dto.getAddress().getDistrictId(), DISTRICT);
        }

        if (dto.getId() == null) {
            validateRequiredField(dto.getTin(), TIN);
            validateExistsInDatabase(customerRepository.existsByTin(dto.getTin()), CUSTOMER, TIN, dto.getTin());
            validateExistsInDatabase(customerRepository.existsByPhone(phone), CUSTOMER, PHONES, phone);
        } else {
            validateExistsInDatabase(customerRepository.existsByTinAndIdNot(dto.getTin(), dto.getId()), CUSTOMER, TIN, dto.getTin());
            validateExistsInDatabase(customerRepository.existsByPhoneAndIdNot(phone, dto.getId()), CUSTOMER, PHONES, phone);

        }
    }
}
