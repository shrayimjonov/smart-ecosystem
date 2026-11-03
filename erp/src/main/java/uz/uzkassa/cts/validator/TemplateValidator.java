package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.template.TemplateDTO;

@Component
public class TemplateValidator extends BaseValidator implements ValidatorStrategy<TemplateDTO> {

    public TemplateValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(TemplateDTO dto) {
        validateRequiredField(dto.getSubjectUzLatin(), NAME_UZ_LATIN);
        validateRequiredField(dto.getSubjectUzCyrillic(), NAME_UZ_CYRILLIC);
        validateRequiredField(dto.getSubjectRu(), NAME_RU);
        validateRequiredField(dto.getMessageUzLatin(), DESCRIPTION_IN_LATIN);
        validateRequiredField(dto.getMessageUzCyrillic(), DESCRIPTION_IN_CYRILLIC);
        validateRequiredField(dto.getMessageRu(), DESCRIPTION_IN_RUSSIAN);
        validateRequiredField(dto.getChannelType(), CHANNEL_TYPE);
        validateRequiredField(dto.getType(), TYPE);
    }
}
