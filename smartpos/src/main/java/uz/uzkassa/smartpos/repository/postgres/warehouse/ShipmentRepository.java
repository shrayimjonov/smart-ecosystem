package uz.uzkassa.smartpos.repository.postgres.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Shipment;

/**
 * Spring Data repository for the Shipment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long>, JpaSpecificationExecutor<Shipment>, ShipmentRepositoryCustom {
    @Query("select max(intNumber) from Shipment where companyId=?1")
    Integer getMaxIntNumber(Long companyId);
}
