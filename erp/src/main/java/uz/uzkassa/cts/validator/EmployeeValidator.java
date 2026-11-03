package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.employee.base.EmployeeDTO;
import uz.uzkassa.cts.repository.UserRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class EmployeeValidator extends BaseValidator implements ValidatorStrategy<EmployeeDTO> {

    UserRepository userRepository;

    public EmployeeValidator(LocalizationMessage localizationMessage, UserRepository userRepository) {
        super(localizationMessage);
        this.userRepository = userRepository;
    }

    @Override
    public void validate(EmployeeDTO dto) {
        validateRequiredField(dto.getFirstName(), FIRST_NAME);
        validateRequiredField(dto.getLastName(), LAST_NAME);
        validateRequiredField(dto.getPhones(), PHONES);
        String phone = dto.getPhones().getFirst().getName();
        validateRequiredField(phone, PHONE);

        if (dto.getId() == null) {
            validateRequiredField(dto.getUsername(), USERNAME);
            validateExistsInDatabase(userRepository.existsByLoginAndDeletedIsFalse(phone), EMPLOYEE, USERNAME, phone);
            validateExistsInDatabase(userRepository.existsByPhoneAndDeletedIsFalse(phone), EMPLOYEE, PHONE, phone);
            validateExistsInDatabase(userRepository.existsByLoginAndDeletedIsFalse(dto.getUsername()), EMPLOYEE, USERNAME, dto.getUsername());
        } else {
            validateExistsInDatabase(userRepository.existsByLoginAndIdNotAndDeletedIsFalse(phone, dto.getId()), EMPLOYEE, USERNAME, phone);
            validateExistsInDatabase(userRepository.existsByPhoneAndIdNotAndDeletedIsFalse(phone, dto.getId()), EMPLOYEE, PHONE, phone);
            validateExistsInDatabase(userRepository.existsByLoginAndIdNotAndDeletedIsFalse(dto.getUsername(), dto.getId()), EMPLOYEE, USERNAME, dto.getUsername());
        }
    }
}
