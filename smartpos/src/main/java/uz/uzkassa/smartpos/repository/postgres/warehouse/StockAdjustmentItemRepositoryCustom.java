package uz.uzkassa.smartpos.repository.postgres.warehouse;

import org.springframework.data.domain.Page;
import uz.uzkassa.smartpos.domain.postgres.warehouse.StockAdjustmentItem;
import uz.uzkassa.smartpos.dto.filter.StockAdjustmentFilter;


public interface StockAdjustmentItemRepositoryCustom {
    Page<StockAdjustmentItem> findAllByFilter(StockAdjustmentFilter filter);
}
