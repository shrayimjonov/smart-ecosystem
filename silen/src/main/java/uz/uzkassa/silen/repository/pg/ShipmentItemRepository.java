package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.ShipmentItem;

import java.util.List;

/**
 * Spring Data repository for the Shipment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShipmentItemRepository extends JpaRepository<ShipmentItem, Long> {
    @EntityGraph(attributePaths = "product")
    List<ShipmentItem> findAllByShipmentIdOrderById(String shipmentId);

    List<ShipmentItem> findAllByShipmentId(String shipmentId);

    List<ShipmentItem> findAllByShipmentIdAndReturnIdIsNull(String shipmentId);

    @Query(value = "select a.id from ShipmentItem a where a.shipmentId=:shipmentId")
    List<Long> findAllIdByShipmentId(@Param("shipmentId") String shipmentId);

    @Query(value = "select nextval('shipment_item_id_seq')", nativeQuery = true)
    Long getNextSequenceValue();

    List<ShipmentItem> findAllByIdIn(List<Long> ids);

}
