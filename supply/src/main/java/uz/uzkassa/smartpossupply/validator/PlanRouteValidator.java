package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.PlanRouteStatus;
import uz.uzkassa.smartpossupply.repository.PlanRouteRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class PlanRouteValidator extends BaseValidator implements ValidatorStrategy<PlanRouteDTO> {

    PlanRouteRepository planRouteRepository;

    public PlanRouteValidator(LocalizationMessage localizationMessage, PlanRouteRepository planRouteRepository) {
        super(localizationMessage);
        this.planRouteRepository = planRouteRepository;
    }

    @Override
    public void validate(PlanRouteDTO dto) {
        validateRequiredField(dto.getTemplateId(), TEMPLATE);
        validateRequiredField(dto.getAgentId(), AGENT);
        validateRequiredField(dto.getFromDate(), DATE);
        validateRequiredField(dto.getToDate(), DATE);
        validateRequiredField(dto.getItems(), ITEMS);
        if (dto.getId() == null) {
            validateExistsInDatabase(planRouteRepository.existsByAgentIdAndStatusAndDeletedIsFalse(dto.getAgentId(), PlanRouteStatus.OPEN), PLAN_ROUTE, AGENT, dto.getAgentId().toString());
        } else {
            validateExistsInDatabase(planRouteRepository.existsByAgentIdAndStatusAndDeletedIsFalseAndIdNot(dto.getAgentId(), PlanRouteStatus.OPEN, dto.getId()), PLAN_ROUTE, AGENT, dto.getAgentId().toString());
        }
    }
}
