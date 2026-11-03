package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.TradeInPriceHistory;
import uz.uzkassa.cts.enums.ContractType;
import uz.uzkassa.cts.enums.DeviceType;

@Repository
public interface TradeInPriceHistoryRepository extends BaseRepository<TradeInPriceHistory, Long> {

    TradeInPriceHistory findFirstByActiveTrueAndDeviceModelId(Long deviceModelId);

    TradeInPriceHistory findFirstByActiveTrueAndContractTypeAndDeviceModelId(ContractType contractType, Long deviceModelId);

    TradeInPriceHistory findFirstByActiveTrueAndContractTypeAndDeviceModelIdAndDeviceType(ContractType contractType, Long deviceModelId, DeviceType deviceType);
}
