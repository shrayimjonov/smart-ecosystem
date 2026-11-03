package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.stocktransfer.StockTransfer;
import uz.uzkassa.horeca.enums.StockTransferStatus;


public interface StockTransferRepository extends BaseRepository<StockTransfer, Long>, StockTransferRepositoryCustom {
    @Query("SELECT coalesce(max(st.incrementNumber), 0) FROM StockTransfer st where st.fromBranchId=:branchId")
    Long getMaxIncrementNumber(@Param("branchId") Long branchId);

    boolean existsByFromWarehouseIdAndStatus(Long warehouseId, StockTransferStatus status);
}
