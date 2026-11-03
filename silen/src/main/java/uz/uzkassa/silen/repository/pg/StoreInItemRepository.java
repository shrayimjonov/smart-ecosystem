package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.silen.domain.StoreInItem;

import java.util.List;
import java.util.Optional;

public interface StoreInItemRepository extends JpaRepository<StoreInItem, Long> {
    List<StoreInItem> findAllByStoreInId(String id);

    Optional<StoreInItem> findFirstByStoreInIdAndProductIdAndSerialId(String storeInId, String productId, String serialId);

    boolean existsByStoreInIdAndProductId(String storeInId, String productId);
}
