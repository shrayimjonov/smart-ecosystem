package uz.uzkassa.cts.repository.custom;

import uz.uzkassa.cts.domain.FiscalModuleReport;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.FiscalModuleReportFilter;

public interface FiscalModuleReportRepositoryCustom {

    ResultList<FiscalModuleReport> getResultList(FiscalModuleReportFilter filter);
}
