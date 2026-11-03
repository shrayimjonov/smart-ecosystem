package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.BankTariffHistory;
import uz.uzkassa.cts.dto.payload.Payload;

public interface BankTariffHistoryService extends BaseService<BankTariffHistory, Long> {

    void create(Long customerId, Long bankTariffId);

    void syncWithSmartPos();

    void syncListener(Payload payload);
}
