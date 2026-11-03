package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.inventory.Warehouse;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.warehouse.WarehouseDTO;
import uz.uzkassa.cts.dto.warehouse.WarehouseDetailAdminDTO;
import uz.uzkassa.cts.dto.warehouse.WarehouseListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface WarehouseService extends BaseService<Warehouse, Long> {

    Long create(WarehouseDTO warehouseDTO);

    Long update(Long id, WarehouseDTO warehouseDTO);

    Page<WarehouseListAdminDTO> getListForAdmin(BaseFilter filter);

    List<CommonDTO> lookUp(BaseFilter filter);

    WarehouseDetailAdminDTO get(Long id);

    void updateStatus(Long id, CommonStatus status);
}
