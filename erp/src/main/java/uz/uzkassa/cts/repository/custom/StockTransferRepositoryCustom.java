package uz.uzkassa.cts.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.stock.StockTransfer;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportDetailListDTO;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.filter.StockTransferFilter;

@Repository
public interface StockTransferRepositoryCustom {

    ResultList<StockTransfer> getResultList(StockTransferFilter filter);

    Page<StockBalanceReportDetailListDTO> getBalanceReportDetails(StockFilter filter);
}
