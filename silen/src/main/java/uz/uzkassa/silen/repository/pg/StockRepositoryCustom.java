package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.dto.filter.WarehouseFilter;
import uz.uzkassa.silen.dto.warehouse.StockStatsDTO;

public interface StockRepositoryCustom {

    Page<StockStatsDTO> findAllByFilter(WarehouseFilter filter);

    StockStatsDTO stats(WarehouseFilter filter);
}
