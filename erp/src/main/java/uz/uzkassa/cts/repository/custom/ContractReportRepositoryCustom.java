package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.report.ContractReportExcelExportDto;
import uz.uzkassa.cts.dto.report.ContractReportListDTO;
import uz.uzkassa.cts.filter.ContractReportFilter;

import java.util.List;

@Repository
public interface ContractReportRepositoryCustom {

    ResultList<ContractReportListDTO> getResultList(ContractReportFilter filter);

    List<ContractReportExcelExportDto> exportExcel(ContractReportFilter filter);
}
