package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.feedback.FeedbackDTO;

@Component
public class FeedbackValidator extends BaseValidator implements ValidatorStrategy<FeedbackDTO> {

    public FeedbackValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(FeedbackDTO dto) {
        validateRequiredField(dto.getRate(), RATE);
        validateRequiredField(dto.getTypeId(), TYPE);
        validateRequiredField(dto.getDescription(), DESCRIPTION);
        validateRequiredField(dto.getCustomerBranchId(), POSITION);
    }
}
