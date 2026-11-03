package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.checklist.ChecklistQuestionnaire;

import java.util.List;

@Repository
public interface ChecklistQuestionnaireRepository extends BaseRepository<ChecklistQuestionnaire, Long> {
    List<ChecklistQuestionnaire> findAllByChecklistId(Long checklistId);
    void deleteAllByChecklistId(Long checklistId);
}
