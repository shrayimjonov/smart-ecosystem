package uz.uzkassa.smartpos.repository.postgres.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductPriceHistory;

import java.util.Collection;
import java.util.List;


public interface ProductPriceHistoryRepository extends JpaRepository<ProductPriceHistory, Long> {
    List<ProductPriceHistory> findAllByProductIdAndBranchIdOrderByUpdatedDateTimeDesc(Long productId, Long branchId);

    void deleteAllByProductIdAndBranchId(Long productId, Long branchId);

    void deleteAllByBranchIdAndProductIdIn(Long branchId, Collection<Long> productIds);

    void deleteAllByBranchId(Long branchId);
}
