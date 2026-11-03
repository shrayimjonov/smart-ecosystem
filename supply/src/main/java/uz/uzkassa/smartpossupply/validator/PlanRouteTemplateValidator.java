package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.planroutetemplate.PlanRouteTemplateDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.repository.PlanRouteTemplateRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class PlanRouteTemplateValidator extends BaseValidator implements ValidatorStrategy<PlanRouteTemplateDTO> {

    PlanRouteTemplateRepository planRouteTemplateRepository;

    public PlanRouteTemplateValidator(LocalizationMessage localizationMessage, PlanRouteTemplateRepository planRouteTemplateRepository) {
        super(localizationMessage);
        this.planRouteTemplateRepository = planRouteTemplateRepository;
    }

    @Override
    public void validate(PlanRouteTemplateDTO dto) {
        if (dto.getId() == null) {
            validateExistsInDatabase(planRouteTemplateRepository.existsByAgentIdAndStatusAndDeletedIsFalse(dto.getAgentId(), CommonStatus.ACTIVE), PLAN_ROUTE, AGENT, dto.getAgentId().toString());
        } else {
            validateExistsInDatabase(planRouteTemplateRepository.existsByAgentIdAndStatusAndIdNotAndDeletedIsFalse(dto.getAgentId(), CommonStatus.ACTIVE, dto.getId()), PLAN_ROUTE, AGENT, dto.getAgentId().toString());
        }
    }
}
