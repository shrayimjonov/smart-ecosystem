package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stockadjustment.StockAdjustment;

import java.util.Optional;

@Repository
public interface StockAdjustmentRepository extends BaseRepository<StockAdjustment, Long>, StockAdjustmentRepositoryCustom {
    @Query("SELECT coalesce(max(sa.incrementNumber), 0) FROM StockAdjustment sa where sa.companyId=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);

    boolean existsStockAdjustmentByAdjustmentNumberAndCompanyIdAndIdNot(String adjustmentNumber, Long companyId, Long id);

    @Query("select sa from StockAdjustment sa where sa.id =:id")
    @EntityGraph(attributePaths = {"branch", "warehouse", "products", "products.product", "products.product.parent", "products.productSerial"})
    Optional<StockAdjustment> getDetail(@Param("id") Long id);
}
