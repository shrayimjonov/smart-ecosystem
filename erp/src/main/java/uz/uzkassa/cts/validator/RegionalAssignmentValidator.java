package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.regionalassignment.RegionalAssignmentDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.repository.RegionalAssignmentRepository;

@Component
public class RegionalAssignmentValidator extends BaseValidator implements ValidatorStrategy<RegionalAssignmentDTO> {

    public RegionalAssignmentValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(RegionalAssignmentDTO dto) {
        validateRequiredField(dto.getRegionId(), REGION);
        validateRequiredField(dto.getEmployeeId(), EMPLOYEE);
        validateRequiredField(dto.getDistrictIds(), DISTRICT);
    }
}
