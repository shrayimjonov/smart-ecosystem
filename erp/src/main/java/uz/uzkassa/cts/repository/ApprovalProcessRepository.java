package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.approval.process.ApprovalProcess;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.Optional;

@Repository
public interface ApprovalProcessRepository extends BaseRepository<ApprovalProcess, Long> {

    Optional<ApprovalProcess> findFirstByCompanyIdAndEntityTypeAndDeletedIsFalse(Long companyId, EntityTypeEnum entityType);
}
