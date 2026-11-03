package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.TradeInContract;
import uz.uzkassa.cts.enums.ContractStatus;

import java.util.List;

@Repository
public interface TradeInContractRepository extends BaseRepository<TradeInContract, Long> {
    List<TradeInContract> findByCustomerTinAndTradeInActIsNullAndStatus(String tin, ContractStatus status);

    @EntityGraph(attributePaths = {"tradeInAct"})
    List<TradeInContract> findByCustomerId(Long customerId);
}
