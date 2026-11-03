package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.billing.UsagePlan;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsagePlanRepository extends BaseRepository<UsagePlan, Long> {

    @EntityGraph(attributePaths = {"company"})
    Optional<UsagePlan> findFirstByCompanyIdAndServiceType(Long companyId, String serviceType);

    List<UsagePlan> findAllByCompanyIdAndQtyIsNotNullAndServiceTypeIsNotNull(Long companyId);
}
