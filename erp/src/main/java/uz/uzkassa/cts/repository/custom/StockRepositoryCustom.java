package uz.uzkassa.cts.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.stock.Stock;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportDetailListDTO;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportStatisticsDTO;
import uz.uzkassa.cts.filter.StockFilter;

@Repository
public interface StockRepositoryCustom {

    ResultList<Stock> getResultList(StockFilter filter);

    StockBalanceReportStatisticsDTO getBalanceReportStatistics(StockFilter filter);

    Page<StockBalanceReportDetailListDTO> getBalanceReportDetails(StockFilter filter);
}
