package uz.uzkassa.smartpossupply.repository.kpi;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpossupply.domain.kpi.salesplan.KPISalesPlan;

import java.util.Optional;

public interface KPISalesPlanRepository extends JpaRepository<KPISalesPlan, Long>, KPISalesPlanRepositoryCustom {

    Optional<KPISalesPlan> findByIdAndCompanyId(Long id, Long companyId);

}
