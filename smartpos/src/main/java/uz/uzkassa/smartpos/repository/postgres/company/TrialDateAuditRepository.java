package uz.uzkassa.smartpos.repository.postgres.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.company.TrialDateAudit;


@Repository
public interface TrialDateAuditRepository extends JpaRepository<TrialDateAudit, Integer> , JpaSpecificationExecutor<TrialDateAudit> {
}
