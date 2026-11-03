package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.permission.RolePermissionDTO;

@Component
public class RolePermissionValidator extends BaseValidator implements ValidatorStrategy<RolePermissionDTO> {

    public RolePermissionValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(RolePermissionDTO dto) {
        validateRequiredField(dto.getRole(), ROLES);
        validateRequiredField(dto.getPermissionCodes(), PERMISSION_CODE);
    }
}
