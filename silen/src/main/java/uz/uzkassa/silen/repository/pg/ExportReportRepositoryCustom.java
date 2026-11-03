package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.dto.filter.ReportFilter;

public interface ExportReportRepositoryCustom {
    Page<ExportReport> findAllByFilter(ReportFilter filter);
}
