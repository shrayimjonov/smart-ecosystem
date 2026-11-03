package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.leaverequestreason.LeaveRequestReasonDTO;

@Component
public class LeaveRequestReasonValidator extends BaseValidator implements ValidatorStrategy<LeaveRequestReasonDTO> {

    public LeaveRequestReasonValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(LeaveRequestReasonDTO dto) {
        validateRequiredField(dto.getNameUzLatin(), NAME_UZ_LATIN);
        validateRequiredField(dto.getNameUzCyrillic(), NAME_UZ_CYRILLIC);
        validateRequiredField(dto.getNameRu(), NAME_UZ_CYRILLIC);
        validateRequiredField(dto.getAbbreviation(), ABBREVIATION);
        validateRequiredField(dto.getType(), TYPE);
    }
}
