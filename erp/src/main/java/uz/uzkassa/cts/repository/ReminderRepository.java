package uz.uzkassa.cts.repository;

import uz.uzkassa.cts.domain.hrms.Reminder;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.List;
import java.util.Optional;

public interface ReminderRepository extends BaseRepository<Reminder, Long> {

    List<Reminder> findAllByEntityIdAndEntityType(Long entityId, EntityTypeEnum entityType);

    void deleteAllByEntityIdAndEntityType(Long entityId, EntityTypeEnum entityType);

    Optional<Reminder> findByEntityIdAndEntityTypeAndJobId(Long entityId, EntityTypeEnum entityType, String jobId);
}
