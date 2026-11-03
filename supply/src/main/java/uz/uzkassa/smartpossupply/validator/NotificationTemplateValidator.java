package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.template.NotificationTemplateDTO;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class NotificationTemplateValidator extends BaseValidator implements ValidatorStrategy<NotificationTemplateDTO> {

    public NotificationTemplateValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(NotificationTemplateDTO dto) {
        validateRequiredField(dto.getSubjectUz(), SUBJECT);
        validateRequiredField(dto.getSubjectRu(), SUBJECT);
        validateRequiredField(dto.getSubjectCyrillic(), SUBJECT);
        validateRequiredField(dto.getDescriptionUz(), DESCRIPTION);
        validateRequiredField(dto.getSubjectCyrillic(), DESCRIPTION);
        validateRequiredField(dto.getSubjectCyrillic(), DESCRIPTION);
        validateRequiredField(dto.getTemplateType(), TEMPLATE_TYPE);
        validateRequiredField(dto.getTemplateType(), APPLICATION_TYPE);
    }
}
