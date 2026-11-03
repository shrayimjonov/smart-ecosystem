package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.BillingPermission;

@Repository
public interface BillingPermissionRepository extends JpaRepository<BillingPermission, Long> {
}
