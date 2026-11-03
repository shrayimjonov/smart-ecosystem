package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Shipment;
import uz.uzkassa.silen.enumeration.ShipmentStatus;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for the Shipment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, String>, ShipmentRepositoryCustom {
    @Query("select max(intNumber) from Shipment where organizationId=?1")
    Integer getMaxIntNumber(String organizationId);

    boolean existsShipmentByOrganizationIdAndNumberIgnoreCase(String organizationId, String number);

    boolean existsShipmentByOrganizationIdAndNumberIgnoreCaseAndIdIsNot(String organizationId, String number, String id);

    List<Shipment> findAllByStatus(ShipmentStatus status);

    boolean existsByIdAndStatus(String shipmentId, ShipmentStatus status);

    Optional<Shipment> findByCustomerId(Long customerId);
}
