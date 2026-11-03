package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.enums.BankTariffHistoryType;

import java.util.List;

@Repository
public interface BankTariffHistoryRepository extends BaseRepository<uz.uzkassa.cts.domain.BankTariffHistory, Long> {

    List<uz.uzkassa.cts.domain.BankTariffHistory> findAllByStatus(BankTariffHistoryType status);
}
