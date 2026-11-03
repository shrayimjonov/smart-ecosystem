package uz.uzkassa.cts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.BankClientDetail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Repository
public interface BankClientDetailRepository extends BaseRepository<BankClientDetail, Long> {
    Set<BankClientDetail> findByBankClientJournal_IdAndApproved(Long id, boolean approved);

    Page<BankClientDetail> findByBankClientJournal_IdOrderByIdDesc(Long id, Pageable pageable);
}
