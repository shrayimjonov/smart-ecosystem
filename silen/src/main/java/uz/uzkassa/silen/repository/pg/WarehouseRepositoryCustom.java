package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.Warehouse;
import uz.uzkassa.silen.dto.dashboard.DashboardFilter;
import uz.uzkassa.silen.dto.filter.WarehouseFilter;
import uz.uzkassa.silen.dto.warehouse.WarehouseOperationStatsDTO;
import uz.uzkassa.silen.dto.warehouse.WarehouseStatsDTO;

import java.util.List;

public interface WarehouseRepositoryCustom {
    Page<Warehouse> findAllByFilter(WarehouseFilter filter);

    List<WarehouseStatsDTO> getWarehouseStats(DashboardFilter filter, Boolean isFull);

    WarehouseOperationStatsDTO warehouseStatsByOperationProduction(WarehouseFilter filter);

    WarehouseOperationStatsDTO warehouseStatsByOperationShipmentBuyer(WarehouseFilter filter);
}
