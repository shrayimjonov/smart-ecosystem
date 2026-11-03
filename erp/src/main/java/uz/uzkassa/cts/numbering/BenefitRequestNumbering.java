package uz.uzkassa.cts.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.repository.BenefitRequestRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BenefitRequestNumbering extends AbstractNumbering {

    BenefitRequestRepository benefitRequestRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.BENEFIT_REQUEST;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return benefitRequestRepository.getMaxIncrementNumber(companyId);
    }
}
