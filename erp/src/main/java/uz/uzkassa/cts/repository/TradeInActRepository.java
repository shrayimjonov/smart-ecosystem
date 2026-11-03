package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.TradeInAct;
import uz.uzkassa.cts.enums.ContractStatus;

import java.util.Optional;

@Repository
public interface TradeInActRepository extends BaseRepository<TradeInAct, Long> {

    TradeInAct findByContract_Id(Long contractId);

    Optional<TradeInAct> findFirstByDeviceIdAndContract_CustomerIdAndStatus(Long deviceId, Long customerId, ContractStatus status);
}
