package uz.uzkassa.smartpos.repository.postgres.fashion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uz.uzkassa.smartpos.domain.postgres.warehouse.FashionStockAdjustmentItem;

import java.util.List;

public interface FashionStockAdjustmentItemRepository extends JpaRepository<FashionStockAdjustmentItem, Long>, JpaSpecificationExecutor<FashionStockAdjustmentItem> {
    void deleteAllByBranchIdAndCityIdAndProductIdIn(Long branchId, Long cityId, List<Long> categoryIds);

    void deleteAllByProductIdAndBranchId(Long id, Long branchId);

    void deleteAllByBranchId(Long branchId);
}