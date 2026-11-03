package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.enumeration.ReportStatus;

import java.util.Optional;

public interface ExportReportRepository extends JpaRepository<ExportReport, Long>, ExportReportRepositoryCustom {

    /**
     * checking is this organization had already pending export report
     * organization must have only one pending export report
     *
     * @param organizationId
     * @param status         {@link ReportStatus default ReportStatus.PENDING}
     * @return
     */
    Optional<ExportReport> findFirstByOrganizationIdAndStatusOrderByCreatedDateDesc(String organizationId, ReportStatus status);

    Optional<ExportReport> findFirstByOrganizationIdAndBatchIdOrderByCreatedDateDesc(String organizationId, String batchId);

        Optional<ExportReport> findFirstByOrganizationIdAndId(String organizationId, Long id);
}
