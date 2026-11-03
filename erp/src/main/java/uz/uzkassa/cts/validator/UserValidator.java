package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.user.UserDTO;
import uz.uzkassa.cts.repository.UserRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserValidator extends BaseValidator implements ValidatorStrategy<UserDTO> {

    UserRepository userRepository;

    public UserValidator(LocalizationMessage localizationMessage, UserRepository userRepository) {
        super(localizationMessage);
        this.userRepository = userRepository;
    }

    @Override
    public void validate(UserDTO dto) {
        validateRequiredField(dto.getUsername(), USERNAME);
        validateRequiredField(dto.getFirstName(), FIRST_NAME);
        validateRequiredField(dto.getLastName(), LAST_NAME);
        validateRequiredField(dto.getPhone(), PHONE);
        validateRequiredField(dto.getRoles(), ROLES);

        if (dto.getId() == null) {
            validateExistsInDatabase(userRepository.existsByLoginAndDeletedIsFalse(dto.getPhone()), USER, PHONE, dto.getPhone());
            validateExistsInDatabase(userRepository.existsByLoginAndDeletedIsFalse(dto.getUsername()), USER, PHONE, dto.getUsername());
        } else {
            validateExistsInDatabase(userRepository.existsByLoginAndIdNotAndDeletedIsFalse(dto.getPhone(), dto.getId()), USER, PHONE, dto.getPhone());
            validateExistsInDatabase(userRepository.existsByLoginAndIdNotAndDeletedIsFalse(dto.getUsername(), dto.getId()), USER, PHONE, dto.getUsername());
        }
    }
}
