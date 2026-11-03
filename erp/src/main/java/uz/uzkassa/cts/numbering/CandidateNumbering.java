package uz.uzkassa.cts.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.repository.CandidateRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CandidateNumbering extends AbstractNumbering {

    CandidateRepository candidateRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.CANDIDATE;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return candidateRepository.getMaxIncrementNumber(companyId);
    }
}
