package uz.uzkassa.smartpossupply.service.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.report.ReportPreviewAudit;
import uz.uzkassa.smartpossupply.domain.report.ReportPreviewAuditStatus;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDatasetType;
import uz.uzkassa.smartpossupply.repository.ReportPreviewAuditRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportPreviewAuditService {

    private final ReportPreviewAuditRepository repository;

    @Transactional(
            transactionManager = "masterTransactionManager",
            propagation = Propagation.REQUIRES_NEW
    )
    public void record(
            UUID executionId,
            Long userId,
            Long companyId,
            ReportDatasetType datasetType,
            String specificationHash,
            ReportPreviewAuditStatus status,
            long durationMs,
            Long rowCount,
            String errorCode
    ) {
        ReportPreviewAudit audit = new ReportPreviewAudit();
        audit.setExecutionId(executionId);
        audit.setUserId(userId);
        audit.setCompanyId(companyId);
        audit.setDatasetType(datasetType);
        audit.setSpecificationHash(specificationHash);
        audit.setStatus(status);
        audit.setDurationMs(durationMs);
        audit.setRowCount(rowCount);
        audit.setErrorCode(errorCode);
        repository.save(audit);
    }
}
