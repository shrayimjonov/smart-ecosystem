package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.Shipment;
import uz.uzkassa.silen.dto.ShipmentHistoryDTO;
import uz.uzkassa.silen.dto.ShipmentStatsDTO;
import uz.uzkassa.silen.dto.dashboard.DashboardStatsDTO;
import uz.uzkassa.silen.dto.filter.ShipmentFilter;
import uz.uzkassa.silen.dto.filter.TopFilter;
import uz.uzkassa.silen.dto.warehouse.WarehouseSelectItem;

import java.math.BigDecimal;
import java.util.List;

public interface ShipmentRepositoryCustom {
    Page<Shipment> findAllByFilter(ShipmentFilter filter);

    List<ShipmentStatsDTO> findAllByFilterForStats(ShipmentFilter filter);

    Page<Shipment> findAllByFilterWithoutOrgId(ShipmentFilter filter);

    Page<WarehouseSelectItem> getShippedProductsStats(ShipmentFilter filter);

    Page<ShipmentHistoryDTO> getShipHistory(ShipmentFilter filter);

    BigDecimal getShipHistorySummary(ShipmentFilter filter);

    List<DashboardStatsDTO> getShippedByProductAndUnit(TopFilter filter);

//    Optional<Shipment> findById(String id);
}
