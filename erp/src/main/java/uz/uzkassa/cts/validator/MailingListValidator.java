package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.maillinglist.MailingListDTO;

@Component
public class MailingListValidator extends BaseValidator implements ValidatorStrategy<MailingListDTO> {

    public MailingListValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(MailingListDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        validateRequiredField(dto.getDescription(), DESCRIPTION);
    }
}
