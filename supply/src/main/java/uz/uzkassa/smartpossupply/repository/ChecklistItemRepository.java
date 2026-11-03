package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.checklist.ChecklistItem;

@Repository
public interface ChecklistItemRepository extends BaseRepository<ChecklistItem, Long> {

    void deleteAllByChecklistId(Long checklistId);
}
