package uz.uzkassa.cts.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.repository.VacancyApplicationRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VacancyApplicationNumbering extends AbstractNumbering {

    VacancyApplicationRepository vacancyApplicationRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.VACANCY_APPLICATION;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return vacancyApplicationRepository.getMaxIncrementNumber(companyId);
    }
}
