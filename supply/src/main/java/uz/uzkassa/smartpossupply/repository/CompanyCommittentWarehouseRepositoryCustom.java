package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.CompanyCommittentWarehouse;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.12.2023
 */
@Repository
public interface CompanyCommittentWarehouseRepositoryCustom {

    ResultList<CompanyCommittentWarehouse> getResultList(BalanceReportFilter filter);

    BigDecimal getReportTotalQty(BalanceReportFilter filter);

    StockProductQtyStatisticsDTO getQtyStatistics(StockFilter filter);

    Page<StockProductQtyDetailListDTO> getQtyDetails(StockFilter filter);
}
