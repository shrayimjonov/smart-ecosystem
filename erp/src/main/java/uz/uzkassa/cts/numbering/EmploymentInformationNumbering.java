package uz.uzkassa.cts.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.repository.EmploymentInformationRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EmploymentInformationNumbering extends AbstractNumbering {

    EmploymentInformationRepository employmentInformationRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.EMPLOYEE;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return employmentInformationRepository.getMaxIncrementNumber(companyId);
    }
}
