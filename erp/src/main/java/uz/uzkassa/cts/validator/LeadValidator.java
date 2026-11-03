package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.lead.LeadDTO;
import uz.uzkassa.cts.utils.Utils;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

@Component
public class LeadValidator extends BaseValidator implements ValidatorStrategy<LeadDTO> {

    public LeadValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(LeadDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        validateRequiredField(dto.getPhone(), PHONE);
        if (!Utils.isValidPhone(dto.getPhone())) {
            throw new BadRequestException(localizationMessage.localize(PHONE_NUMBER_INVALID_FORMAT));
        }
    }
}
