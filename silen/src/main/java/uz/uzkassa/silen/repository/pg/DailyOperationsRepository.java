package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.DailyOperations;

import java.util.Optional;

@Repository
public interface DailyOperationsRepository extends DailyOperationsRepositoryCustom, JpaRepository<DailyOperations, Integer> {

    Optional<DailyOperations> findFirstByOrganizationIdAndProductIdOrderByCreatedDateDesc(String organizationId, String productId);

    Optional<DailyOperations> findFirstByOrganizationIdAndProductIdAndSerialIdOrderByCreatedDateDesc(String organizationId, String productId, String serialId);
}
