package uz.uzkassa.smartpossupply.repository.kpi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.kpi.KPI;

import java.util.Optional;

@Repository
public interface KPIRepository extends JpaRepository<KPI, Long>, KPIRepositoryCustom {

    Long deleteByIdAndCompanyId(Long id, Long companyId);

    Optional<KPI> findByIdAndCompanyId(Long id, Long companyId);

    Optional<KPI> findByIdAndCompanyIdAndDeletedIsFalse(Long id,  Long companyId);
}
