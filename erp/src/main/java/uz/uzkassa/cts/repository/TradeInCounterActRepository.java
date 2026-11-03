package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.TradeInCounterAct;

@Repository
public interface TradeInCounterActRepository extends BaseRepository<TradeInCounterAct, Long> {
    TradeInCounterAct findByContract_Id(Long contractId);
}
