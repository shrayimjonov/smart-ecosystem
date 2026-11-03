package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.exportimport.ExportReport;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 15.02.2023 16:32
 */
@Repository
public interface ExportReportRepository extends BaseRepository<ExportReport, Long>, ExportReportRepositoryCustom {

    List<ExportReport> findAllByExpireDateBefore(LocalDateTime expireDate);
}
