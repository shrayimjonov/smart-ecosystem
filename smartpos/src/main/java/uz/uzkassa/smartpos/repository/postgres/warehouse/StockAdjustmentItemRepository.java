package uz.uzkassa.smartpos.repository.postgres.warehouse;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.warehouse.StockAdjustmentItem;

import java.util.Collection;

@Repository
public interface StockAdjustmentItemRepository extends JpaRepository<StockAdjustmentItem, Long>, StockAdjustmentItemRepositoryCustom {
    void deleteAllByProductIdAndBranchId(Long productId, Long branchId);

    void deleteAllByBranchIdAndCityIdAndProductIdIn(@NotNull Long branchId, @NotNull Long cityId, Collection<Long> categoryIds);

    @Modifying
    @Query(value = "delete from stock_adjustment_item sai using product_branch pb where sai.product_id = pb.product_id and sai.branch_id = pb.branch_id and sai.city_id = pb.city_id and pb.deleted", nativeQuery = true)
    void cleanup();

    void deleteAllByBranchId(@NotNull Long branchId);
}
