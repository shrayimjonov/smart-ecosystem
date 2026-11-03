package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.stock.Stock;
import uz.uzkassa.horeca.enums.TransferType;


public interface StockRepository extends BaseRepository<Stock, Long>, StockRepositoryCustom {
    @Query("SELECT coalesce(max(s.incrementNumber), 0) FROM Stock s where s.branchId=:branchId and s.transferType=:transferType")
    Long getMaxIncrementNumber(@Param("branchId") Long branchId, @Param("transferType") TransferType transferType);
}
