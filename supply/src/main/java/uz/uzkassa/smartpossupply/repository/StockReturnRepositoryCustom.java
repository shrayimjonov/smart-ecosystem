package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.StockFilter;

@Repository
public interface StockReturnRepositoryCustom {

    ResultList<Stock> getResultList(StockFilter filter);
}
