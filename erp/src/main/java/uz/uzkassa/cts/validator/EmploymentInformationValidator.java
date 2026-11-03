package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.employmentInformation.EmploymentInformationDTO;

@Component
public class EmploymentInformationValidator extends BaseValidator implements ValidatorStrategy<EmploymentInformationDTO> {

    public EmploymentInformationValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(EmploymentInformationDTO dto) {
        validateRequiredField(dto.getCompanyId(), COMPANY);
        validateRequiredField(dto.getDepartmentId(), DEPARTMENT);
        validateRequiredField(dto.getPositionId(), POSITION);
        validateRequiredField(dto.getHireDate(), DATE);
        validateRequiredField(dto.getTimeslotId(), TIMESLOT);
        validateRequiredField(dto.getType(), TYPE);
    }
}
