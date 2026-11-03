package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.transaction.Transaction;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.02.2023 19:33
 */
@Repository
public interface TransactionRepository extends BaseRepository<Transaction, Long>, TransactionRepositoryCustom {

    @Query("SELECT coalesce(max(t.incrementNumber), 0) FROM Transaction t where t.companyId=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);
}
