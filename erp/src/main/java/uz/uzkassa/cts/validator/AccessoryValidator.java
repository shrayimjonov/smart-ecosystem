package uz.uzkassa.cts.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.accessory.AccessoryDTO;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccessoryValidator extends BaseValidator implements ValidatorStrategy<AccessoryDTO> {

    public AccessoryValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(AccessoryDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        validateRequiredField(dto.getType(), TYPE);
        validateRequiredField(dto.getCatalog().getCode(), CODE);
        validateRequiredField(dto.getCatalog().getName(), NAME);
        validateRequiredField(dto.getApackage().getName(), NAME);
        validateRequiredField(dto.getApackage().getCode(), CODE);
        validateRequiredField(dto.getCode(), CODE);
    }
}
