package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.exportimport.ExportReport;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.ExportReportStatus;
import uz.uzkassa.cts.repository.custom.ExportReportRepositoryCustom;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExportReportRepository extends BaseRepository<ExportReport, Long>, ExportReportRepositoryCustom {

    List<ExportReport> findAllByExpireDateBefore(LocalDateTime expireDate);

    boolean existsByReportEntityTypeAndContentTypeAndStatusAndCreatorIdAndCreatedDateGreaterThan(EntityTypeEnum reportEntityType, ContentType contentType, ExportReportStatus status, Long creatorId, LocalDateTime createdDate);
}
