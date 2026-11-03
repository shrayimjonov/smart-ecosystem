package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.stock.CurrentStock;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.MenuItemFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 10.11.2024
 */
public interface CurrentStockRepositoryCustom {

    ResultList<CurrentStock> getResultList(MenuItemFilter filter);
}
