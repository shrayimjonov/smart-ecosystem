package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.uzcard.UzcardConnectionDTO;

@Component
public class UzcardConnectionValidator extends BaseValidator implements ValidatorStrategy<UzcardConnectionDTO> {

    public UzcardConnectionValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(UzcardConnectionDTO dto) {
        validateRequiredField(dto.getSerialNum(), SERIAL_NUMBER);
    }
}
