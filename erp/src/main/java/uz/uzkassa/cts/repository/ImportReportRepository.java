package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.exportimport.ImportReport;

@Repository
public interface ImportReportRepository extends BaseRepository<ImportReport, Long>, ImportReportRepositoryCustom {
}
