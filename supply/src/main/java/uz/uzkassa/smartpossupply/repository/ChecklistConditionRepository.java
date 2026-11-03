package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.checklist.ChecklistCondition;

@Repository
public interface ChecklistConditionRepository extends BaseRepository<ChecklistCondition, Long> {

    void deleteAllByChecklistId(Long checklistId);
}
