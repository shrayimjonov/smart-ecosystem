package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.permission.PermissionDTO;
import uz.uzkassa.cts.repository.PermissionRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class PermissionValidator extends BaseValidator implements ValidatorStrategy<PermissionDTO> {

    PermissionRepository permissionRepository;

    public PermissionValidator(LocalizationMessage localizationMessage, PermissionRepository permissionRepository) {
        super(localizationMessage);
        this.permissionRepository = permissionRepository;
    }

    @Override
    public void validate(PermissionDTO dto) {
        validateRequiredField(dto.getNameUzLatin(), NAME_UZ_LATIN);
        validateRequiredField(dto.getNameUzCyrillic(), NAME_UZ_CYRILLIC);
        validateRequiredField(dto.getNameRu(), NAME_RU);
        validateRequiredField(dto.getCode(), CODE);
        validateRequiredField(dto.getType(), PERMISSION_TYPE);

        if (dto.getId() == null) {
            validateExistsInDatabase(permissionRepository.existsByCodeAndDeletedIsFalse(dto.getCode()), PERMISSION, CODE, dto.getCode());
        } else {
            validateExistsInDatabase(permissionRepository.existsByCodeAndDeletedIsFalseAndIdNot(dto.getCode(), dto.getId()), PERMISSION, CODE, dto.getCode());
        }
    }
}
