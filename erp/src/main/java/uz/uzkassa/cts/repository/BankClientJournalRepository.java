package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.BankClientJournal;

@Repository
public interface BankClientJournalRepository extends BaseRepository<BankClientJournal, Long> {
    @Query(value = "select j from BankClientJournal j " +
            " where j.id = (select max(ju.id) from BankClientJournal ju)")
    BankClientJournal findLastBankClientJournal();
}
