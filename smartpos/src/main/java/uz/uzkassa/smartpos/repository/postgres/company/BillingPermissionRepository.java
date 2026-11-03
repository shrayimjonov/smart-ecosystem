package uz.uzkassa.smartpos.repository.postgres.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.company.BillingPermission;

import java.util.Set;

@Repository
public interface BillingPermissionRepository extends JpaRepository<BillingPermission, Long> {
    @Query(value = "select distinct company_id from billing_permission", nativeQuery = true)
    Set<Long> getcompanyIds();
}
