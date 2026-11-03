package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.chat.ChatDTO;

@Component
public class ChatValidator extends BaseValidator implements ValidatorStrategy<ChatDTO> {

    public ChatValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(ChatDTO dto) {
        validateRequiredField(dto.getEntityId(), ENTITY);
        validateRequiredField(dto.getEntityType(), ENTITY_TYPE);
        validateRequiredField(dto.getMessage(), MESSAGE);
    }
}
