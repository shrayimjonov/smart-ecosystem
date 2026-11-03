package uz.uzkassa.cts.repository;


import org.springframework.data.domain.Page;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportListDTO;
import uz.uzkassa.cts.filter.StockFilter;

import java.math.BigDecimal;

public interface WarehouseItemRepositoryCustom {

    Page<StockBalanceReportListDTO> getReport(StockFilter filter);

    BigDecimal getReportTotalQty(StockFilter filter);

    BigDecimal getVariationsQty(StockFilter filter);
}
