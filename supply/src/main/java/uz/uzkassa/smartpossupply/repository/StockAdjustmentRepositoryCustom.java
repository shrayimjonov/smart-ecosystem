package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stockadjustment.StockAdjustment;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.StockAdjustmentFilter;

@Repository
public interface StockAdjustmentRepositoryCustom {
    ResultList<StockAdjustment> getResultList(StockAdjustmentFilter filter);
}
