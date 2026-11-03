package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.salaryAllocation.SalaryAllocationItemDTO;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class SalaryAllocationValidator extends BaseValidator implements ValidatorStrategy<SalaryAllocationItemDTO> {

    public SalaryAllocationValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(SalaryAllocationItemDTO dto) {
        validateRequiredField(dto.getPositionId(), POSITION);
        validateRequiredField(dto.getGradeId(), GRADE);
        validateRequiredField(dto.getMinSalaryAmount(), MIN_SALARY_AMOUNT);
        validateRequiredField(dto.getMaxSalaryAmount(), MAX_SALARY_AMOUNT);
        validateRequiredField(dto.getMaxEmployeeCount(), MAX_EMPLOYEE_COUNT);
        validateRequiredField(dto.getMinEmployeeCount(), MIN_EMPLOYEE_COUNT);
        if (dto.getMinSalaryAmount().compareTo(dto.getMaxSalaryAmount()) > 0 || dto.getMinEmployeeCount() > dto.getMaxEmployeeCount()) {
            throw new BadRequestException(localizationMessage.localize(MIN_EXCEEDS_MAX));
        }
    }
}
