package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.salaryAllocation.SalaryAllocationDTO;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class SalaryAllocationItemValidator extends BaseValidator implements ValidatorStrategy<SalaryAllocationDTO> {

    public SalaryAllocationItemValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(SalaryAllocationDTO dto) {
        validateRequiredField(dto.getDepartmentId(), DEPARTMENT);
        validateRequiredField(dto.getItems(), ITEMS);
    }
}
