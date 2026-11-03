package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.checklist.ChecklistDTO;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ChecklistValidator extends BaseValidator implements ValidatorStrategy<ChecklistDTO> {

    public ChecklistValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(ChecklistDTO dto) {
        validateRequiredField(dto.getEntityId(), ID);
        validateRequiredField(dto.getEntityType(), CHECKLIST);
        validateRequiredField(dto.getItems(), ITEMS);
        validateRequiredField(dto.getConditions(), CONDITIONS);
    }
}
