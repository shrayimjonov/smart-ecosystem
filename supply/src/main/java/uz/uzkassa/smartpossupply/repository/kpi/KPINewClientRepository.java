package uz.uzkassa.smartpossupply.repository.kpi;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpossupply.domain.kpi.client.KPINewClient;
import uz.uzkassa.smartpossupply.enums.KPIStatus;
import uz.uzkassa.smartpossupply.enums.KPIType;
import uz.uzkassa.smartpossupply.enums.Role;

import java.util.List;
import java.util.Optional;

public interface KPINewClientRepository extends JpaRepository<KPINewClient, Long>, KPINewClientRepositoryCustom {

    boolean existsByRoleAndTypeAndCompanyIdAndDeletedIsFalse(Role role, KPIType kpiType, Long companyId);

    boolean existsByIdNotAndRoleAndTypeAndCompanyIdAndDeletedIsFalse(Long id, Role role, KPIType kpiType, Long companyId);

    List<KPINewClient> findAllByCompanyIdAndDeletedIsFalseAndStatus(Long companyId, KPIStatus status);

    Optional<KPINewClient> findByCompanyIdAndDeletedIsFalseAndStatus(Long companyId, KPIStatus status);
}
