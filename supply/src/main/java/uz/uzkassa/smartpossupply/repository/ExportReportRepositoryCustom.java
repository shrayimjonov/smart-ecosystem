package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.exportimport.ExportReport;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.02.2023 10:30
 */
@Repository
public interface ExportReportRepositoryCustom {
    ResultList<ExportReport> getResultList(BaseFilter filter);
}
