package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.leaverequest.LeaveRequestDTO;

@Component
public class LeaveRequestValidator extends BaseValidator implements ValidatorStrategy<LeaveRequestDTO> {

    public LeaveRequestValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(LeaveRequestDTO dto) {
        validateRequiredField(dto.getFromDate(), DATE);
        validateRequiredField(dto.getToDate(), DATE);
        validateRequiredField(dto.getDescription(), DESCRIPTION);
        validateRequiredField(dto.getReasonId(), REASON);
        validateRequiredField(dto.getApprovers(), APPROVER);
    }
}
