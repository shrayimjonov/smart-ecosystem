package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.leaveallowance.LeaveAllowanceDTO;
import uz.uzkassa.cts.repository.LeaveAllowanceRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LeaveAllowanceValidator extends BaseValidator implements ValidatorStrategy<LeaveAllowanceDTO> {

    LeaveAllowanceRepository leaveAllowanceRepository;

    public LeaveAllowanceValidator(LocalizationMessage localizationMessage, LeaveAllowanceRepository leaveAllowanceRepository) {
        super(localizationMessage);
        this.leaveAllowanceRepository = leaveAllowanceRepository;
    }

    @Override
    public void validate(LeaveAllowanceDTO dto) {
        validateRequiredField(dto.getEmployeeId(), EMPLOYEE);
        validateRequiredField(dto.getYear(), YEAR);

        if (dto.getId() == null) {
            validateExistsInDatabase(leaveAllowanceRepository.existsByEmployeeIdAndYearAndDeletedIsFalse(dto.getEmployeeId(), dto.getYear()), EMPLOYEE, YEAR, dto.getYear().toString());
        } else {
            validateExistsInDatabase(leaveAllowanceRepository.existsByEmployeeIdAndYearAndDeletedIsFalseAndIdNot(dto.getEmployeeId(), dto.getYear(), dto.getId()), EMPLOYEE, YEAR, dto.getYear().toString());
            ;
        }
    }
}
