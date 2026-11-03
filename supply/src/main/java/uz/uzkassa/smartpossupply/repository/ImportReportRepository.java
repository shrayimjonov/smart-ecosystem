package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.exportimport.ImportReport;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.03.2023 16:32
 */
@Repository
public interface ImportReportRepository extends BaseRepository<ImportReport, Long>, ImportReportRepositoryCustom {
}
