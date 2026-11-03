package uz.uzkassa.smartpos.repository.clickhouse.receipt;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.dto.customerreceipts.BranchReceiptsDTO;
import uz.uzkassa.smartpos.dto.customerreceipts.BranchReceiptsStatDTO;
import uz.uzkassa.smartpos.dto.filter.ShiftFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.CustomerReceiptsFilter;
import uz.uzkassa.smartpos.dto.shift.ShiftReportStatDTO;
import uz.uzkassa.smartpos.filter.ShiftReportFilter;

@Repository
public interface ClickhouseReceiptReportByBranch {
    Page<BranchReceiptsDTO> getReceiptsAmountByBranch(CustomerReceiptsFilter filter);

    BranchReceiptsStatDTO getReceiptsAmountByBranchStat(CustomerReceiptsFilter filter);

    ShiftReportStatDTO getStatsForZReport(ShiftFilter filter);

    ShiftReportStatDTO getStats(ShiftFilter filter);

    ShiftReportStatDTO getStats(ShiftReportFilter filter);
}
