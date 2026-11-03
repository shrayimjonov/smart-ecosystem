package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.checklist.Condition;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.repository.ConditionRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ConditionService extends BaseService {

    ConditionRepository conditionRepository;

    @Transactional
    public Long create(CommonDTO conditionDTO) {
        Condition condition = new Condition();
        condition.setName(conditionDTO.getName());
        condition.setCompanyId(getCurrentCompanyId());
        conditionRepository.save(condition);
        return condition.getId();
    }

    @Transactional
    public Long update(Long id, CommonDTO conditionDTO) {
        return conditionRepository.findById(id).map(condition -> {
            condition.setName(conditionDTO.getName());
            conditionRepository.save(condition);
            return condition.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Condition.class.getSimpleName(), "id", id));
    }

    public List<CommonDTO> getItems() {
        return conditionRepository.findAllByCompanyId(getCurrentCompanyId())
            .stream()
            .map(SimpleEntity::toCommonDTO)
            .toList();
    }

    public CommonDTO get(Long id) {
        return conditionRepository
            .findById(id)
            .map(SimpleEntity::toCommonDTO)
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Condition.class.getSimpleName(), "id", id));
    }
}
