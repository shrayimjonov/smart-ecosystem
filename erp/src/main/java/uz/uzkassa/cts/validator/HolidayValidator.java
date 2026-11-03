package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.holiday.HolidayDTO;

@Component
public class HolidayValidator extends BaseValidator implements ValidatorStrategy<HolidayDTO> {

    public HolidayValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(HolidayDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        validateRequiredField(dto.getHolidayDate(), DATE);
        validateRequiredField(dto.getCompanyId(), COMPANY);
    }
}
