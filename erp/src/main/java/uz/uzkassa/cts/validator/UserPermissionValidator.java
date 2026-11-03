package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.permission.UserPermissionDTO;

@Component
public class UserPermissionValidator extends BaseValidator implements ValidatorStrategy<UserPermissionDTO> {

    public UserPermissionValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(UserPermissionDTO dto) {
        validateRequiredField(dto.getUserId(), USER);
        validateRequiredField(dto.getPermissionCodes(), PERMISSION_CODE);
    }
}
