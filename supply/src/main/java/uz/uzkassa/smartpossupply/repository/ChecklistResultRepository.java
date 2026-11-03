package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.checklist.ChecklistResult;

@Repository
public interface ChecklistResultRepository extends BaseRepository<ChecklistResult, Long> {

    void deleteAllByChecklistItem_ChecklistId(Long checklistId);
}
