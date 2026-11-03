package uz.uzkassa.silen.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.redis.RedisShipmentItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface RedisShipmentCodeRepository extends CrudRepository<RedisShipmentItem, Long> {

    List<RedisShipmentItem> findAllByShipmentIdOrderById(String shipmentId);

    List<RedisShipmentItem> findAllByShipmentId(String shipmentId);

    Optional<RedisShipmentItem> findFirstByShipmentIdAndProductId(String shipmentId, String productId);
}
