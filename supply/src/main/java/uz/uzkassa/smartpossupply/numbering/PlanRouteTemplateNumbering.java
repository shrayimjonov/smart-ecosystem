package uz.uzkassa.smartpossupply.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.repository.PlanRouteTemplateRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PlanRouteTemplateNumbering extends AbstractNumbering {

    PlanRouteTemplateRepository planRouteTemplateRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.PLAN_ROUTE_TEMPLATE;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return planRouteTemplateRepository.getMaxIncrementNumber(companyId);
    }
}
