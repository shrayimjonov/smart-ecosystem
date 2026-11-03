package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransferProduct;

import java.util.Optional;
import java.util.Set;

@Repository
public interface StockTransferProductRepository extends BaseRepository<StockTransferProduct, Long>, StockTransferProductRepositoryCustom {

    @Modifying
    @Query("update StockTransferProduct  set deleted=true where id in (:stockTransferProductIds)")
    void deleteAllByIds(@Param("stockTransferProductIds") Set<Long> stockTransferProductIds);

    Optional<StockTransferProduct> findByFromProductIdAndStockTransferId(Long id, Long stockTransferId);
}
