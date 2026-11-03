package uz.uzkassa.cts.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.caseXizmat.CaseXizmatDTO;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CaseXizmatValidator extends BaseValidator implements ValidatorStrategy<CaseXizmatDTO> {

    public CaseXizmatValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(CaseXizmatDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        validateRequiredField(dto.getXizmatId(), XIZMAT);
        validateRequiredField(dto.getType(), TYPE);
    }
}
