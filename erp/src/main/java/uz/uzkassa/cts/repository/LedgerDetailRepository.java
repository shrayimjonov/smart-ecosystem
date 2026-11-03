package uz.uzkassa.cts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.LedgerDetail;

import java.math.BigDecimal;

@Repository
public interface LedgerDetailRepository extends BaseRepository<LedgerDetail, Long> {

    LedgerDetail findFirstByTinOrderByIdDesc(String tin);

    LedgerDetail findFirstByCustomerIdOrderByIdDesc(Long customerId);

    LedgerDetail findFirstByAccountNumberOrderByIdDesc(String accountNumber);

    @EntityGraph(attributePaths = "ledger")
    Page<LedgerDetail> findByCustomerIdOrderByIdDesc(Long customerId, Pageable pageable);

    @Query(value = "select sum(ld.credit) from ledger_detail ld" +
            " inner join customer c on c.id= ld.customer_id  where c.tin = ?1 or c.old_tin = ?1 ", nativeQuery = true)
    BigDecimal getTotalCredit(String tin);

    Page<LedgerDetail> findByAccountNumberOrderByIdAsc(String accountNumber, Pageable pageable);
}
