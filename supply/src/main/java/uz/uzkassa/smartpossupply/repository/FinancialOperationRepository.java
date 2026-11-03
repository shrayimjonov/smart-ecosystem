package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.FinancialOperation;
import uz.uzkassa.smartpossupply.enums.FinancialOperationType;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 09.01.2024
 */
@Repository
public interface FinancialOperationRepository extends BaseRepository<FinancialOperation, Long>, FinancialOperationRepositoryCustom {

    @Query("SELECT coalesce(max(fo.incrementNumber), 0) FROM FinancialOperation fo where fo.companyId=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);

    Optional<FinancialOperation> findFirstByStockOutIdAndOperationTypeAndDeletedIsFalse(Long stockOutId, FinancialOperationType operationType);

    @Query("""
            select coalesce(sum(fo.amount), 0) from FinancialOperation fo
            where fo.stockOutId = :stockOutId
                        and fo.operationType = :operationType
                        and fo.deleted = false
            """)
    BigDecimal sumAmountByStockOutId(Long stockOutId, FinancialOperationType operationType);

    @Query("select f.id from FinancialOperation f where f.stockOutId = :stockOutId and f.deleted = false")
    Long findIdByStockOutId(@Param("stockOutId") Long stockOutId);
}
