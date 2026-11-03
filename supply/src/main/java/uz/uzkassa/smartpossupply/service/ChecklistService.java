package uz.uzkassa.smartpossupply.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.checklist.Checklist;
import uz.uzkassa.smartpossupply.domain.checklist.ChecklistCondition;
import uz.uzkassa.smartpossupply.domain.checklist.ChecklistItem;
import uz.uzkassa.smartpossupply.domain.checklist.ChecklistQuestionnaire;
import uz.uzkassa.smartpossupply.domain.checklist.ChecklistResult;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRoute;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.checklist.ChecklistDTO;
import uz.uzkassa.smartpossupply.dto.checklist.ChecklistDetailDTO;
import uz.uzkassa.smartpossupply.dto.checklist.ChecklistItemDetailDTO;
import uz.uzkassa.smartpossupply.dto.checklist.ChecklistListDTO;
import uz.uzkassa.smartpossupply.dto.checklist.ChecklistQuestionDTO;
import uz.uzkassa.smartpossupply.dto.checklist.ChecklistResultDTO;
import uz.uzkassa.smartpossupply.enums.ChecklistStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.filters.ChecklistFilter;
import uz.uzkassa.smartpossupply.repository.ChecklistConditionRepository;
import uz.uzkassa.smartpossupply.repository.ChecklistItemRepository;
import uz.uzkassa.smartpossupply.repository.ChecklistQuestionnaireRepository;
import uz.uzkassa.smartpossupply.repository.ChecklistRepository;
import uz.uzkassa.smartpossupply.repository.ChecklistResultRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ChecklistService extends BaseService {

    ChecklistRepository checklistRepository;
    ChecklistItemRepository checklistItemRepository;
    ChecklistConditionRepository checklistConditionRepository;
    ChecklistResultRepository checklistResultRepository;
    ChecklistQuestionnaireRepository checklistQuestionnaireRepository;
    EntityManager entityManager;
    ValidationContext validationContext;

    @Transactional
    public Long create(ChecklistDTO checklistDTO) {
        validationContext.validate(checklistDTO);
        Checklist checklist = new Checklist();
        checklist.setName(checklistDTO.getName());
        checklist.setEntityId(checklistDTO.getEntityId());
        checklist.setEntityType(checklistDTO.getEntityType());
        checklist.setCompanyId(getCurrentCompanyId());
        checklist.setSupervisorId(checklistDTO.getSupervisorId());
        if (EntityTypeEnum.PLAN_ROUTE == checklistDTO.getEntityType()) {
            PlanRoute planRoute = entityManager.find(PlanRoute.class, checklistDTO.getEntityId());
            checklist.setAgentId(planRoute.getAgentId());
        }
        checklistRepository.save(checklist);

        List<ChecklistItem> items = checklistDTO.getItems().stream().map(checklistItemDTO -> {
            ChecklistItem checklistItem = new ChecklistItem();
            checklistItem.setChecklistId(checklist.getId());
            checklistItem.setEntityId(checklistItemDTO.getEntityId());
            checklistItem.setEntityType(checklistItemDTO.getEntityType());
            return checklistItem;
        }).toList();
        checklistItemRepository.saveAll(items);

        List<ChecklistCondition> checklistConditions = checklistDTO.getConditions().stream().map(conditionId -> {
            ChecklistCondition checklistCondition = new ChecklistCondition();
            checklistCondition.setChecklistId(checklist.getId());
            checklistCondition.setConditionId(conditionId);
            return checklistCondition;
        }).toList();
        checklistConditionRepository.saveAll(checklistConditions);

        List<ChecklistResult> checklistResults = new ArrayList<>();
        for (ChecklistCondition checklistCondition : checklistConditions) {
            for (ChecklistItem checklistItem : items) {
                ChecklistResult checklistResult = new ChecklistResult();
                checklistResult.setChecklistConditionId(checklistCondition.getId());
                checklistResult.setChecklistItemId(checklistItem.getId());
                checklistResults.add(checklistResult);
            }
        }
        checklistResultRepository.saveAll(checklistResults);

        if (!CollectionUtils.isEmpty(checklistDTO.getQuestions())) {
            List<ChecklistQuestionnaire> questionnaires = checklistDTO.getQuestions().stream().map(question -> {
                ChecklistQuestionnaire checklistQuestionnaire = new ChecklistQuestionnaire();
                checklistQuestionnaire.setChecklistId(checklist.getId());
                checklistQuestionnaire.setQuestion(question);
                return checklistQuestionnaire;
            }).toList();
            checklistQuestionnaireRepository.saveAll(questionnaires);
        }
        return checklist.getId();
    }

    @Transactional
    public Long update(Long id, ChecklistDTO checklistDTO) {
        return checklistRepository.findById(id).map(checklist -> {
            checklist.setName(checklistDTO.getName());
            checklist.setEntityId(checklistDTO.getEntityId());
            checklist.setEntityType(checklistDTO.getEntityType());
            checklist.setSupervisorId(checklistDTO.getSupervisorId());
            if (EntityTypeEnum.PLAN_ROUTE == checklistDTO.getEntityType()) {
                PlanRoute planRoute = entityManager.find(PlanRoute.class, checklistDTO.getEntityId());
                checklist.setAgentId(planRoute.getAgentId());
            }

            checklistItemRepository.deleteAllByChecklistId(checklist.getId());
            checklistConditionRepository.deleteAllByChecklistId(checklist.getId());
            checklistResultRepository.deleteAllByChecklistItem_ChecklistId(checklist.getId());
            checklistQuestionnaireRepository.deleteAllByChecklistId(checklist.getId());

            List<ChecklistItem> items = checklistDTO.getItems().stream().map(checklistItemDTO -> {
                ChecklistItem checklistItem = new ChecklistItem();
                checklistItem.setChecklistId(checklist.getId());
                checklistItem.setEntityId(checklistItemDTO.getEntityId());
                checklistItem.setEntityType(checklistItemDTO.getEntityType());
                return checklistItem;
            }).toList();
            checklistItemRepository.saveAll(items);

            List<ChecklistCondition> checklistConditions = checklistDTO.getConditions().stream().map(conditionId -> {
                ChecklistCondition checklistCondition = new ChecklistCondition();
                checklistCondition.setChecklistId(checklist.getId());
                checklistCondition.setConditionId(conditionId);
                return checklistCondition;
            }).toList();
            checklistConditionRepository.saveAll(checklistConditions);

            List<ChecklistResult> checklistResults = new ArrayList<>();
            for (ChecklistCondition checklistCondition : checklistConditions) {
                for (ChecklistItem checklistItem : items) {
                    ChecklistResult checklistResult = new ChecklistResult();
                    checklistResult.setChecklistConditionId(checklistCondition.getId());
                    checklistResult.setChecklistItemId(checklistItem.getId());
                    checklistResults.add(checklistResult);
                }
            }
            checklistResultRepository.saveAll(checklistResults);
            if (!CollectionUtils.isEmpty(checklistDTO.getQuestions())) {
                List<ChecklistQuestionnaire> questionnaires = checklistDTO.getQuestions().stream().map(question -> {
                    ChecklistQuestionnaire checklistQuestionnaire = new ChecklistQuestionnaire();
                    checklistQuestionnaire.setChecklistId(checklist.getId());
                    checklistQuestionnaire.setQuestion(question);
                    return checklistQuestionnaire;
                }).toList();
                checklistQuestionnaireRepository.saveAll(questionnaires);
            }
            return checklist.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Checklist.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void updateStatus(Long id, ChecklistStatus status) {
        checklistRepository
            .findById(id)
            .ifPresentOrElse(checklist -> {
                checklist.setStatus(status);
                if (ChecklistStatus.IN_PROGRESS == status) {
                    checklist.setChecklistDate(LocalDateTime.now());
                }
                checklistRepository.save(checklist);
            }, () -> validationService.entityNotFoundExceptionThrow(Checklist.class.getSimpleName(), "id", id).get());
    }

    public Page<ChecklistListDTO> getList(ChecklistFilter filter) {
        ResultList<Checklist> resultList = checklistRepository.getResultList(filter);
        List<ChecklistListDTO> result = resultList
            .getList()
            .stream()
            .map(checklist -> {
                ChecklistListDTO checklistListDTO = new ChecklistListDTO();
                checklistListDTO.setId(checklist.getId());
                checklistListDTO.setName(checklist.getName());
                checklistListDTO.setChecklistDate(checklist.getChecklistDate());
                checklistListDTO.setAgent(checklist.getAgent().toCommonDTO());
                checklistListDTO.setSupervisor(checklist.getSupervisor().toCommonDTO());
                checklistListDTO.setStatus(checklist.getStatus());
                return checklistListDTO;
            })
            .toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public ChecklistDetailDTO getBySupervisor(Long supervisorId) {
        Checklist checklist = checklistRepository.getBySupervisorId(supervisorId).orElse(null);
        if (checklist == null) {
            return null;
        }
        return this.wrap(checklist);
    }

    public ChecklistDetailDTO get(Long id) {
        return checklistRepository
            .getDetail(id)
            .map(this::wrap)
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Checklist.class.getSimpleName(), "id", id));
    }

    private ChecklistDetailDTO wrap(Checklist checklist) {
        ChecklistDetailDTO checklistDetailDTO = new ChecklistDetailDTO();
        checklistDetailDTO.setId(checklist.getId());
        checklistDetailDTO.setName(checklist.getName());
        checklistDetailDTO.setChecklistDate(checklist.getChecklistDate());
        checklistDetailDTO.setEntityId(checklist.getEntityId());
        checklistDetailDTO.setEntityType(checklist.getEntityType());
        if (checklist.getAgent() != null) {
            checklistDetailDTO.setAgent(checklist.getAgent().toCommonDTO());
        }
        if (EntityTypeEnum.PLAN_ROUTE == checklist.getEntityType()) {
            checklistDetailDTO.setEntity(entityManager.find(PlanRoute.class, checklist.getEntityId()).toCommonDTO());
        }
        checklistDetailDTO.setSupervisor(checklist.getSupervisor().toCommonDTO());
        checklistDetailDTO.setStatus(checklist.getStatus());
        List<ChecklistItemDetailDTO> items = checklist.getItems().stream().map(checklistItem -> {
            ChecklistItemDetailDTO checklistListItemDetailDTO = new ChecklistItemDetailDTO();
            checklistListItemDetailDTO.setId(checklistItem.getId());
            checklistListItemDetailDTO.setEntityId(checklistItem.getEntityId());
            checklistListItemDetailDTO.setEntityType(checklistItem.getEntityType());
            if (EntityTypeEnum.CUSTOMER_BRANCH == checklistItem.getEntityType()) {
                checklistListItemDetailDTO.setEntity(entityManager.find(CustomerBranch.class, checklistItem.getEntityId()).toCommonDTO());
            }
            List<ChecklistResultDTO> result = checklistItem.getResults().stream().map(checklistResult -> {
                ChecklistResultDTO checklistResultDTO = new ChecklistResultDTO();
                checklistResultDTO.setId(checklistResult.getId());
                checklistResultDTO.setChecked(checklistResult.getChecked());
                checklistResultDTO.setCondition(checklistResult.getChecklistCondition().toCommonDTO());
                return checklistResultDTO;
            }).toList();
            checklistListItemDetailDTO.setResult(result);
            return checklistListItemDetailDTO;
        }).toList();
        checklistDetailDTO.setItems(items);

        checklistDetailDTO.setConditions(checklist.getConditions().stream().map(SimpleEntity::toCommonDTO).toList());
        checklistDetailDTO.setQuestions(checklist.getQuestions().stream().map(checklistQuestionnaire -> {
            ChecklistQuestionDTO checklistQuestionDTO = new ChecklistQuestionDTO();
            checklistQuestionDTO.setId(checklistQuestionnaire.getId());
            checklistQuestionDTO.setQuestion(checklistQuestionnaire.getQuestion());
            checklistQuestionDTO.setAnswer(checklistQuestionnaire.getAnswer());
            return checklistQuestionDTO;
        }).toList());

        return checklistDetailDTO;
    }

    @Transactional
    public void check(Long resultId, Boolean checked) {
        checklistResultRepository.findById(resultId).ifPresentOrElse(checklistResult -> {
            checklistResult.setChecked(checked);
            checklistResultRepository.save(checklistResult);
        }, () -> validationService.entityNotFoundExceptionThrow(ChecklistResult.class.getSimpleName(), "id", resultId).get());
    }
}
