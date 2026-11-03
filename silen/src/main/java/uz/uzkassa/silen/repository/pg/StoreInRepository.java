package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.StoreIn;

import java.util.Optional;

@Repository
public interface StoreInRepository extends JpaRepository<StoreIn, String>, StoreInRepositoryCustom {
    boolean existsStoreInByOrganizationIdAndNumberIgnoreCase(String organizationId, String number);

    @Query(value = "SELECT coalesce(max(s.intNumber), 1) from StoreIn s where s.organizationId = :organizationId")
    Integer getMaxNumberByOrganizationId(@Param("organizationId") String organizationId);

    Optional<StoreIn> findFirstByShipmentIdOrderByCreatedDate(String shipmentId);


}
