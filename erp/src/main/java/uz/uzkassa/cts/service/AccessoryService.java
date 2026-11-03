package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Accessory;
import uz.uzkassa.cts.dto.accessory.AccessoryDTO;
import uz.uzkassa.cts.dto.accessory.AccessoryDetailDTO;
import uz.uzkassa.cts.dto.accessory.AccessoryListDTO;
import uz.uzkassa.cts.dto.accessory.AccessoryLookupDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface AccessoryService extends BaseService<Accessory, Long> {

    Long create(AccessoryDTO accessoryDTO);

    Long update(Long id, AccessoryDTO accessoryDTO);

    AccessoryDetailDTO get(Long id);

    Page<AccessoryListDTO> getList(BaseFilter filter);

    List<AccessoryLookupDTO> lookUp(BaseFilter filter);

    void updateStatus(Long id, CommonStatus status);
}
