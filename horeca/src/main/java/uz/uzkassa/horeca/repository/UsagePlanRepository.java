package uz.uzkassa.horeca.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.horeca.domain.billing.UsagePlan;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsagePlanRepository extends BaseRepository<UsagePlan, Long> {
    Optional<UsagePlan> findFirstByCompanyIdAndServiceType(Long companyId, String serviceType);

    List<UsagePlan> findAllByCompanyIdAndQtyIsNotNullAndServiceTypeIsNotNull(Long companyId);
}
