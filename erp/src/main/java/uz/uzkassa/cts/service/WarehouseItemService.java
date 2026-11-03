package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.inventory.WarehouseItem;
import uz.uzkassa.cts.domain.stock.Stock;
import uz.uzkassa.cts.domain.stock.StockAdjustment;
import uz.uzkassa.cts.domain.stock.StockTransfer;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportListDTO;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportDetailListDTO;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportStatisticsDTO;
import uz.uzkassa.cts.filter.StockFilter;

import java.math.BigDecimal;

public interface WarehouseItemService extends BaseService<WarehouseItem, Long> {

    Page<StockBalanceReportListDTO> getBalanceReport(StockFilter filter);

    BigDecimal getBalanceReportTotalQty(StockFilter filter);

    void in(Stock stock);

    void out(Stock stock);

    void in(StockAdjustment stockAdjustment);

    void out(StockAdjustment stockAdjustment);

    void in(StockTransfer stockTransfer);

    void out(StockTransfer stockTransfer);

    BigDecimal getVariationsQty(StockFilter filter);

    StockBalanceReportStatisticsDTO getBalanceReportStatistics(StockFilter filter);

    Page<StockBalanceReportDetailListDTO> getBalanceReportDetails(StockFilter filter);
}
