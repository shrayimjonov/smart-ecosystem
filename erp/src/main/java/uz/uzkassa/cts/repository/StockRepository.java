package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.stock.Stock;
import uz.uzkassa.cts.enums.StockTransferType;
import uz.uzkassa.cts.repository.custom.StockRepositoryCustom;

@Repository
public interface StockRepository extends BaseRepository<Stock, Long>, StockRepositoryCustom {

    @Query("SELECT coalesce(max(s.incrementNumber), 0) FROM Stock s where s.companyId=:companyId and s.transferType=:transferType")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId, @Param("transferType") StockTransferType transferType);
}
