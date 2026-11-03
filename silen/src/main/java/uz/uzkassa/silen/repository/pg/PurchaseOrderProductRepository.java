package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.PurchaseOrderProduct;

import java.util.Optional;

@Repository
public interface PurchaseOrderProductRepository extends JpaRepository<PurchaseOrderProduct, Long> {

    Optional<PurchaseOrderProduct> findFirstByOrderIdAndProductId(Long orderId, String productId);

    Optional<PurchaseOrderProduct> findFirstByIdNotAndOrderIdAndProductId(Long id, Long orderId, String productId);
}
