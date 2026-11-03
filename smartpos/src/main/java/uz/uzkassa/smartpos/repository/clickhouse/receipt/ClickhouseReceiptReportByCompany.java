package uz.uzkassa.smartpos.repository.clickhouse.receipt;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.dto.customerreceipts.CompanyReceiptsDTO;
import uz.uzkassa.smartpos.dto.customerreceipts.CompanyReceiptsStatDTO;
import uz.uzkassa.smartpos.dto.filter.receipt.CustomerReceiptsFilter;

@Repository
public interface ClickhouseReceiptReportByCompany {

    Page<CompanyReceiptsDTO> getReceiptsAmountByCompany(CustomerReceiptsFilter filter);

    CompanyReceiptsStatDTO getReceiptsAmountByCompanyStat(CustomerReceiptsFilter filter);
}
