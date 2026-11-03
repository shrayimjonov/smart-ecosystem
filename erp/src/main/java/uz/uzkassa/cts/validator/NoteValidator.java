package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.note.NoteDTO;

@Component
public class NoteValidator extends BaseValidator implements ValidatorStrategy<NoteDTO> {

    public NoteValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(NoteDTO dto) {
        validateRequiredField(dto.getDescription(), DESCRIPTION);
        validateRequiredField(dto.getEntityType(), ENTITY_TYPE);
        validateRequiredField(dto.getEntityId(), ENTITY);
    }
}
