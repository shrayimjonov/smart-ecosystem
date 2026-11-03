package uz.uzkassa.smartpossupply.service.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpossupply.domain.report.ReportPreviewAuditStatus;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDatasetType;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewRequest;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewResponse;
import uz.uzkassa.smartpossupply.report.engine.ReportException;
import uz.uzkassa.smartpossupply.report.engine.ReportQueryEngine;
import uz.uzkassa.smartpossupply.report.engine.ReportQueryPlan;
import uz.uzkassa.smartpossupply.report.engine.ReportSecurityContext;
import uz.uzkassa.smartpossupply.report.execution.ReportExecutionResult;
import uz.uzkassa.smartpossupply.report.execution.ReportJdbcExecutor;
import uz.uzkassa.smartpossupply.report.execution.ReportPreviewConcurrencyGuard;
import uz.uzkassa.smartpossupply.report.execution.ReportPreviewResponseAssembler;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportPreviewService {

    private final ReportQueryEngine queryEngine;
    private final ReportJdbcExecutor jdbcExecutor;
    private final ReportPreviewConcurrencyGuard concurrencyGuard;
    private final ReportPreviewResponseAssembler responseAssembler;
    private final ReportPreviewAuditService auditService;
    private final ObjectMapper objectMapper;

    public ReportPreviewResponse preview(
            UUID executionId,
            ReportPreviewRequest request,
            ReportSecurityContext securityContext
    ) {
        long startedAt = System.nanoTime();
        String specificationHash = specificationHash(request);
        try {
            ReportQueryPlan plan = queryEngine.build(request, securityContext);
            try (ReportPreviewConcurrencyGuard.Lease ignored = concurrencyGuard.acquire(securityContext.companyId())) {
                ReportExecutionResult result = jdbcExecutor.execute(executionId, plan);
                ReportPreviewResponse response = responseAssembler.assemble(
                        request,
                        result,
                        securityContext.effectiveLanguage()
                );
                recordAudit(
                        executionId,
                        securityContext,
                        datasetType(request),
                        specificationHash,
                        ReportPreviewAuditStatus.SUCCESS,
                        elapsedMillis(startedAt),
                        (long) response.rows().size(),
                        null
                );
                return response;
            }
        } catch (RuntimeException exception) {
            String errorCode = exception instanceof ReportException reportException
                    ? reportException.getCode().name()
                    : "INTERNAL_ERROR";
            recordAudit(
                    executionId,
                    securityContext,
                    datasetType(request),
                    specificationHash,
                    ReportPreviewAuditStatus.FAILED,
                    elapsedMillis(startedAt),
                    null,
                    errorCode
            );
            throw exception;
        }
    }

    public void cancel(UUID executionId) {
        jdbcExecutor.cancel(executionId);
    }

    private String specificationHash(ReportPreviewRequest request) {
        try {
            byte[] json = objectMapper.writeValueAsBytes(request);
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(json));
        } catch (JsonProcessingException exception) {
            return sha256(String.valueOf(request));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private String sha256(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private ReportDatasetType datasetType(ReportPreviewRequest request) {
        return request == null ? null : request.datasetType();
    }

    private long elapsedMillis(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }

    private void recordAudit(
            UUID executionId,
            ReportSecurityContext securityContext,
            ReportDatasetType datasetType,
            String specificationHash,
            ReportPreviewAuditStatus status,
            long durationMs,
            Long rowCount,
            String errorCode
    ) {
        try {
            auditService.record(
                    executionId,
                    securityContext == null ? null : securityContext.userId(),
                    securityContext == null ? null : securityContext.companyId(),
                    datasetType,
                    specificationHash,
                    status,
                    durationMs,
                    rowCount,
                    errorCode
            );
        } catch (RuntimeException auditException) {
            log.error("Could not persist report preview audit for execution {}", executionId, auditException);
        }
    }
}
