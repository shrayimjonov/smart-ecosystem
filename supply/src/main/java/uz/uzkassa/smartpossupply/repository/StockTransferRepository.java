package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransfer;

import java.util.Optional;

@Repository
public interface StockTransferRepository extends BaseRepository<StockTransfer, Long>, StockTransferRepositoryCustom {
    @Query("SELECT coalesce(max(st.incrementNumber), 0) FROM StockTransfer st where st.companyId=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);

    boolean existsStockTransferByTransferNumberAndCompanyIdAndIdNot(String transferNumber, Long companyId, Long id);

    boolean existsByTripIdAndIdNot(Long tripId, Long id);

    @Query("select s from StockTransfer s where s.id=:id")
    @EntityGraph(attributePaths = {"expeditor", "fromBranch", "toBranch", "fromWarehouse", "fromWarehouse.parent", "toWarehouse", "toWarehouse.parent", "stock", "products", "products.fromProduct", "products.fromProduct.parent", "products.order", "products.unit", "products.productSerial"})
    Optional<StockTransfer> getDetail(@Param("id") Long id);

    @EntityGraph(attributePaths = {"trip"})
    Optional<StockTransfer> findByIdAndCompanyIdAndTripIdAndDeletedIsFalse(Long id, Long companyId, Long tripId);
}
