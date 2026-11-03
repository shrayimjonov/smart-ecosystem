package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.domain.warehouse.WarehouseProduct;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.stock.CommonStockStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportDTO;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;

@Repository
public interface StockInRepositoryCustom {

    ResultList<Stock> getResultList(StockFilter filter);

    ResultList<WarehouseProduct> getBalanceReport(ProductFilter filter);

    Page<StockReportDTO> getReport(ProductFilter filter);

    CommonStockStatisticsDTO getStatistic(StockFilter filter);
}
