package uz.uzkassa.smartpossupply.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.repository.PlanRouteRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PlanRouteNumbering extends AbstractNumbering {

    PlanRouteRepository planRouteRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.PLAN_ROUTE;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return planRouteRepository.getMaxIncrementNumber(companyId);
    }
}
