package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.cases.CaseAgreement;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.billing.AgreementStatus;

import java.util.Optional;

@Repository
public interface CaseOperationRepository extends BaseRepository<CaseAgreement, Long> {
    Optional<CaseAgreement> findByEntityIdAndEntityType(Long entityId, EntityTypeEnum entityType);

    boolean existsByEntityIdAndEntityType(Long entityId, EntityTypeEnum entityType);

    boolean existsByEntityIdAndEntityTypeAndAgreementStatus(Long entityId, EntityTypeEnum entityType, AgreementStatus agreementStatus);
}
