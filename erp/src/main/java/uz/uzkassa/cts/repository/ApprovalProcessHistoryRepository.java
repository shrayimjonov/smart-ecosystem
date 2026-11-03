package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.approval.process.ApprovalProcessHistory;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.List;

@Repository
public interface ApprovalProcessHistoryRepository extends BaseRepository<ApprovalProcessHistory, Long> {

    List<ApprovalProcessHistory> findAllByEntityIdAndEntityType(Long entityId, EntityTypeEnum entityType);
}
