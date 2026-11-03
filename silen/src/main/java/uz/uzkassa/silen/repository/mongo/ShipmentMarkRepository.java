package uz.uzkassa.silen.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.config.CacheConstants;
import uz.uzkassa.silen.domain.mongo.ShipmentMark;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentMarkRepository extends MongoRepository<ShipmentMark, Long>, CacheConstants {

    List<ShipmentMark> findAllByShipmentItemIdAndUnitIsNull(Long shipmentItemId);

    List<ShipmentMark> findAllByShipmentItemIdAndUnit(Long shipmentItemId, Integer unit);

    Long countAllByShipmentItemIdAndUnit(Long shipmentItemId, Integer unit);

    List<ShipmentMark> findAllByShipmentItemId(Long shipmentItemId);

    Optional<ShipmentMark> findFirstByCodeAndShipmentItemIdIsNotNull(String code);

    Optional<ShipmentMark> findFirstByCode(String code);

    Optional<ShipmentMark> findFirstByCodeIn(Collection<String> code);

    Optional<ShipmentMark> findFirstByCodeOrCode(String code18, String code20);

    boolean existsShipmentMarkByCode(String code);

    void deleteAllByShipmentItemId(Long shipmentItemId);

    void deleteAllByShipmentItemIdIsNull();

    void deleteAllByCode(String string);

    void deleteByCode(String string);

    boolean existsByCode(String code);
}
