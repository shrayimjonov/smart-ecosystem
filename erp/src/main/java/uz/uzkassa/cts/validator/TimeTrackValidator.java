package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.timetrack.CameraTimeTrackDTO;

@Component
public class TimeTrackValidator extends BaseValidator implements ValidatorStrategy<CameraTimeTrackDTO> {

    public TimeTrackValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(CameraTimeTrackDTO dto) {
        validateRequiredField(dto.getUserId(), EMPLOYEE);
        validateRequiredField(dto.getDateTime(), DATE);
        validateRequiredField(dto.getType(), TYPE);
    }
}
