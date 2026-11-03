package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.stock.Stock;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.StockFilter;

public interface StockRepositoryCustom {
    ResultList<Stock> getResultList(StockFilter filter);
}
