package uz.uzkassa.smartpossupply.domain.report;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDatasetType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_preview_audit")
@Getter
@Setter
public class ReportPreviewAudit extends BaseEntity {

    @Column(name = "execution_id", nullable = false, unique = true)
    private UUID executionId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "company_id")
    private Long companyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "dataset_type", length = 32)
    private ReportDatasetType datasetType;

    @Column(name = "specification_hash", nullable = false, length = 64)
    private String specificationHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private ReportPreviewAuditStatus status;

    @Column(name = "duration_ms", nullable = false)
    private long durationMs;

    @Column(name = "row_count")
    private Long rowCount;

    @Column(name = "error_code", length = 64)
    private String errorCode;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();
}
