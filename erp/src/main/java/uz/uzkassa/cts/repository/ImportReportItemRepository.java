package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.exportimport.ImportReportItem;

@Repository
public interface ImportReportItemRepository extends BaseRepository<ImportReportItem, Long> {
}
