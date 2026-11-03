package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.WarehouseHistory;
import uz.uzkassa.cts.dto.device.DeviceHistoryDTO;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;

public interface WarehouseHistoryService extends BaseService<WarehouseHistory, Long> {

    void create(String entityName, Long entityId, EntityTypeEnum entityType, DeviceStatus status);

    Page<DeviceHistoryDTO> getHistoryList(BaseFilter filter, boolean fiscalModule);
}
