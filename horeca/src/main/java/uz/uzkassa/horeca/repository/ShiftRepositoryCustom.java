package uz.uzkassa.horeca.repository;


import uz.uzkassa.horeca.domain.shift.Shift;
import uz.uzkassa.horeca.domain.stockadjustment.StockAdjustment;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.11.2024
 */
public interface ShiftRepositoryCustom {

    ResultList<Shift> getResultList(BaseFilter filter);
}
