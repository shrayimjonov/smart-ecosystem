package uz.uzkassa.cts.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.accessorymodel.AccessoryModelDTO;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccessoryModelValidator extends BaseValidator implements ValidatorStrategy<AccessoryModelDTO> {


    public AccessoryModelValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(AccessoryModelDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        validateRequiredField(dto.getDescription(), DESCRIPTION);
    }
}
