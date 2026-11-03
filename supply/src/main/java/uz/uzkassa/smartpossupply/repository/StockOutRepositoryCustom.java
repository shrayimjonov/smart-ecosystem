package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.order.terminal.TerminalStockOutStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.reconciliationact.CalculationDTO;
import uz.uzkassa.smartpossupply.dto.stock.ActiveCustomerBaseDTO;
import uz.uzkassa.smartpossupply.dto.stock.CommonStockStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.ActiveCustomerFilter;
import uz.uzkassa.smartpossupply.filters.ReconciliationActFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;

import java.util.List;

@Repository
public interface StockOutRepositoryCustom {

    ResultList<Stock> getResultList(StockFilter filter);

    List<Stock> lookUp(StockFilter filter);

    CommonStockStatisticsDTO getStatistic(StockFilter filter);

    CalculationDTO calculationDeliveryAmount(ReconciliationActFilter filter);

    ResultList<ActiveCustomerBaseDTO> getActiveCustomerBase(ActiveCustomerFilter filter);

    TerminalStockOutStatisticsDTO getStockOutStatisticForTerminal(StockFilter filter);
}
