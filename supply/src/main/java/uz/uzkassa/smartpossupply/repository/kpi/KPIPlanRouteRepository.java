package uz.uzkassa.smartpossupply.repository.kpi;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.kpi.planroute.KPIPlanRoute;
import uz.uzkassa.smartpossupply.enums.KPIStatus;
import uz.uzkassa.smartpossupply.enums.KPIType;
import uz.uzkassa.smartpossupply.enums.Role;

import java.util.List;

@Repository
public interface KPIPlanRouteRepository extends JpaRepository<KPIPlanRoute, Long>, KPIPlanRouteRepositoryCustom {
    boolean existsByRoleAndTypeAndCompanyIdAndDeletedIsFalse(Role role, KPIType type, Long companyId);

    boolean existsByIdNotAndRoleAndTypeAndCompanyIdAndDeletedIsFalse(Long id, Role role, KPIType type, Long companyId);

    @EntityGraph(attributePaths = {"employees"})
    List<KPIPlanRoute> findKPIPlanRouteByStatusAndCompanyIdAndDeletedIsFalse(KPIStatus status, Long companyId);
}
