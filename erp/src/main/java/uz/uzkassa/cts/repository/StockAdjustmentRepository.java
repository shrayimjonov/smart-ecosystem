package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.stock.StockAdjustment;
import uz.uzkassa.cts.repository.custom.StockAdjustmentRepositoryCustom;

@Repository
public interface StockAdjustmentRepository extends BaseRepository<StockAdjustment, Long>, StockAdjustmentRepositoryCustom {

    @Query("SELECT coalesce(max(sa.incrementNumber), 0) FROM StockAdjustment sa where sa.companyId=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);
}
