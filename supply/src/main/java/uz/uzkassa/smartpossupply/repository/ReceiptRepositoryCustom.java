package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.receipt.Receipt;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.receipt.ReceiptStatisticDTO;
import uz.uzkassa.smartpossupply.dto.receipt.TerminalDashboardStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.ReceiptFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 22.12.2022 11:01
 */
@Repository
public interface ReceiptRepositoryCustom {
    ResultList<Receipt> getResultList(ReceiptFilter filter);

    TerminalDashboardStatisticsDTO getTerminalDashboardStatistics(ReceiptFilter filter);

    ReceiptStatisticDTO getStatistics(ReceiptFilter filter);
}
