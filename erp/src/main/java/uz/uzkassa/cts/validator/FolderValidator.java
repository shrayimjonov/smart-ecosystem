package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.document.FolderDTO;

@Component
public class FolderValidator extends BaseValidator implements ValidatorStrategy<FolderDTO> {

    public FolderValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(FolderDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        validateRequiredField(dto.getPermissionCode(), PERMISSION_CODE);
    }
}
