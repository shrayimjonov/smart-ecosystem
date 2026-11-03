package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.exportimport.ExportReport;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;

@Repository
public interface ExportReportRepositoryCustom {

    ResultList<ExportReport> getResultList(BaseFilter filter);
}
