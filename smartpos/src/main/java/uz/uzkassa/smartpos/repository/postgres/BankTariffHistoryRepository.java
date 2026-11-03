package uz.uzkassa.smartpos.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.company.BankTariffHistory;

@Repository
public interface BankTariffHistoryRepository extends JpaRepository<BankTariffHistory, Long> {
}