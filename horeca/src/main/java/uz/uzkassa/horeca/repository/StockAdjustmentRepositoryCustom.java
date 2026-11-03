package uz.uzkassa.horeca.repository;


import uz.uzkassa.horeca.domain.stockadjustment.StockAdjustment;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;

public interface StockAdjustmentRepositoryCustom {
    ResultList<StockAdjustment> getResultList(BaseFilter filter);
}
