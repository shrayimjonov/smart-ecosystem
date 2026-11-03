package uz.uzkassa.smartpos.repository.clickhouse.receipt;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.dto.analytics.*;
import uz.uzkassa.smartpos.dto.filter.receipt.ReceiptFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesDynamicsFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesStatsFilter;

import java.util.List;

@Repository
public interface ClickhouseReceiptSalesStatsRepository {

    EmployeeSalesStatDTO getEmployeeSalesStat(SalesStatsFilter filter);

    List<OverallSalesStatsDTO> getSalesDynamic(SalesDynamicsFilter filter);

    ReceiptStatDto getReceiptStat(ReceiptFilter receiptFilter);

    ReceiptStatisticByStatusesDTO getReceiptStatisticByStatuses(ReceiptFilter filter);

    OverallSalesStatsDTO getSalesStats(SalesStatsFilter filter);

    Page<UserSalesStatsDTO> getUserSalesStats(SalesStatsFilter filter, boolean forExcel);

}
