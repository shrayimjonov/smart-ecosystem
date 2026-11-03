package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.DeviceSuspendHistory;
import uz.uzkassa.cts.dto.devicesuspendhistory.admin.DeviceSuspendHistoryListAdminDTO;
import uz.uzkassa.cts.filter.DeviceSuspendHistoryFilter;

public interface DeviceSuspendHistoryService extends BaseService<DeviceSuspendHistory, Long> {

    Page<DeviceSuspendHistoryListAdminDTO> getListForAdmin(DeviceSuspendHistoryFilter filter);
}
