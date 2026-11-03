package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.stockadjustment.StockAdjustment;

public interface StockAdjustmentRepository extends BaseRepository<StockAdjustment, Long>, StockAdjustmentRepositoryCustom {
    @Query("SELECT coalesce(max(sa.incrementNumber), 0) FROM StockAdjustment sa where sa.branchId=:branchId")
    Long getMaxIncrementNumber(@Param("branchId") Long branchId);
}
