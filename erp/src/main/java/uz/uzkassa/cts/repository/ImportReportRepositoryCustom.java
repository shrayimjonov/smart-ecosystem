package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.exportimport.ImportReport;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;

@Repository
public interface ImportReportRepositoryCustom {
    ResultList<ImportReport> getResultList(BaseFilter filter);
}
