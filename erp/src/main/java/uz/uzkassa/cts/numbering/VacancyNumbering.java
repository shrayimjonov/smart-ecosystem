package uz.uzkassa.cts.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.repository.VacancyRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VacancyNumbering extends AbstractNumbering {

    VacancyRepository vacancyRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.VACANCY;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return vacancyRepository.getMaxIncrementNumber(companyId);
    }
}
