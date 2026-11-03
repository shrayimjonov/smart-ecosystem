package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.DeviceSuspendHistory;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.DeviceSuspendHistoryFilter;

@Repository
public interface DeviceSuspendHistoryRepositoryCustom {

    ResultList<DeviceSuspendHistory> getResultList(DeviceSuspendHistoryFilter filter);
}
