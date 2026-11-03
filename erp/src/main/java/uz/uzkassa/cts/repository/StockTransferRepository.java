package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.stock.StockTransfer;
import uz.uzkassa.cts.repository.custom.StockTransferRepositoryCustom;

@Repository
public interface StockTransferRepository extends BaseRepository<StockTransfer, Long>, StockTransferRepositoryCustom {

    @Query("SELECT coalesce(max(st.incrementNumber), 0) FROM StockTransfer st where st.companyId=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);
}
