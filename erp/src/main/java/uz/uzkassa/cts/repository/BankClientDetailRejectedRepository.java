package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.BankClientDetailRejected;
import uz.uzkassa.cts.enums.BankClientRejectedStatus;

import java.util.List;

@Repository
public interface BankClientDetailRejectedRepository extends BaseRepository<BankClientDetailRejected, Long> {
    List<BankClientDetailRejected> findByStatusAndIdInOrderByBankClientJournalId(BankClientRejectedStatus bankClientRejectedStatus, List<Long> ids);

    BankClientDetailRejected findByStatusAndId(BankClientRejectedStatus bankClientRejectedStatus, Long id);
    BankClientDetailRejected findByStatusInAndId(List<BankClientRejectedStatus> statuses, Long id);
}
