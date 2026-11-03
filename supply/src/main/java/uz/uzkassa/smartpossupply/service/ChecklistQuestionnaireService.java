package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.checklist.ChecklistQuestionnaire;
import uz.uzkassa.smartpossupply.dto.checlistquestionnaire.ChecklistQuestionnaireBatchDTO;
import uz.uzkassa.smartpossupply.dto.checlistquestionnaire.ChecklistQuestionnaireDTO;
import uz.uzkassa.smartpossupply.dto.checlistquestionnaire.ChecklistQuestionnaireDetailDTO;
import uz.uzkassa.smartpossupply.dto.checlistquestionnaire.ChecklistQuestionnaireListDTO;
import uz.uzkassa.smartpossupply.dto.checlistquestionnaire.mobile.ChecklistQuestionAnswerMobileDTO;
import uz.uzkassa.smartpossupply.dto.checlistquestionnaire.mobile.ChecklistQuestionnaireListMobileDTO;
import uz.uzkassa.smartpossupply.repository.ChecklistQuestionnaireRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ChecklistQuestionnaireService extends BaseService {

    ChecklistQuestionnaireRepository checklistQuestionnaireRepository;

    @Transactional
    public Long create(ChecklistQuestionnaireDTO checklistQuestionnaireDTO) {
        ChecklistQuestionnaire checklistQuestionnaire = new ChecklistQuestionnaire();
        checklistQuestionnaire.setChecklistId(checklistQuestionnaireDTO.getChecklistId());
        checklistQuestionnaire.setQuestion(checklistQuestionnaireDTO.getQuestion());
        checklistQuestionnaireRepository.save(checklistQuestionnaire);
        return checklistQuestionnaire.getId();
    }

    @Transactional
    public void createBatch(ChecklistQuestionnaireBatchDTO checklistQuestionnaireBatchDTO) {
        List<ChecklistQuestionnaire> checklistQuestionnaires = checklistQuestionnaireBatchDTO.getQuestions().stream().map(question -> {
            ChecklistQuestionnaire checklistQuestionnaire = new ChecklistQuestionnaire();
            checklistQuestionnaire.setChecklistId(checklistQuestionnaireBatchDTO.getChecklistId());
            checklistQuestionnaire.setQuestion(question);
            return checklistQuestionnaire;
        }).toList();
        checklistQuestionnaireRepository.saveAll(checklistQuestionnaires);
    }

    @Transactional
    public Long update(Long id, ChecklistQuestionnaireDTO checklistQuestionnaireDTO) {
        return checklistQuestionnaireRepository.findById(id).map(checklistQuestion -> {
            checklistQuestion.setChecklistId(checklistQuestionnaireDTO.getChecklistId());
            checklistQuestion.setQuestion(checklistQuestionnaireDTO.getQuestion());
            checklistQuestionnaireRepository.save(checklistQuestion);
            return checklistQuestion.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(ChecklistQuestionnaire.class.getSimpleName(), "id", id));
    }

    public List<ChecklistQuestionnaireListDTO> getList(Long checklistId) {
        return checklistQuestionnaireRepository.findAllByChecklistId(checklistId)
                .stream()
                .map(checklistQuestion -> {
                    ChecklistQuestionnaireListDTO checklistQuestionnaireListDTO = new ChecklistQuestionnaireListDTO();
                    checklistQuestionnaireListDTO.setId(checklistQuestion.getId());
                    checklistQuestionnaireListDTO.setQuestion(checklistQuestion.getQuestion());
                    return checklistQuestionnaireListDTO;
                })
                .toList();
    }

    public ChecklistQuestionnaireDetailDTO get(Long id) {
        return checklistQuestionnaireRepository
                .findById(id)
                .map(checklistQuestion -> {
                    ChecklistQuestionnaireDetailDTO checklistQuestionnaireDetailDTO = new ChecklistQuestionnaireDetailDTO();
                    checklistQuestionnaireDetailDTO.setId(checklistQuestion.getId());
                    checklistQuestionnaireDetailDTO.setQuestion(checklistQuestion.getQuestion());
                    return checklistQuestionnaireDetailDTO;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(ChecklistQuestionnaire.class.getSimpleName(), "id", id));
    }

    public List<ChecklistQuestionnaireListMobileDTO> getByChecklist(Long checklistId) {
        return checklistQuestionnaireRepository
                .findAllByChecklistId(checklistId)
                .stream()
                .map(checklistQuestionnaire -> {
                    ChecklistQuestionnaireListMobileDTO checklistQuestionDetailDTO = new ChecklistQuestionnaireListMobileDTO();
                    checklistQuestionDetailDTO.setId(checklistQuestionnaire.getId());
                    checklistQuestionDetailDTO.setQuestion(checklistQuestionnaire.getQuestion());
                    checklistQuestionDetailDTO.setAnswer(checklistQuestionnaire.getAnswer());
                    return checklistQuestionDetailDTO;
                }).toList();
    }

    @Transactional
    public void answer(Long id, ChecklistQuestionAnswerMobileDTO checklistQuestionAnswerMobileDTO) {
        checklistQuestionnaireRepository.findById(id).ifPresentOrElse(checklistQuestionnaire -> {
            checklistQuestionnaire.setAnswer(checklistQuestionAnswerMobileDTO.getAnswer());
            checklistQuestionnaireRepository.save(checklistQuestionnaire);
        }, () -> validationService.entityNotFoundExceptionThrow(ChecklistQuestionnaire.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void deleteBatch(List<Long> ids) {
        checklistQuestionnaireRepository.deleteAllByIdInBatch(ids);
    }
}
