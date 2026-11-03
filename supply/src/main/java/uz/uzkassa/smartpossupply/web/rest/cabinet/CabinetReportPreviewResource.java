package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;
import uz.uzkassa.smartpossupply.constants.AuthConstants;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewRequest;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewResponse;
import uz.uzkassa.smartpossupply.enums.LanguageEnum;
import uz.uzkassa.smartpossupply.report.engine.ReportErrorCode;
import uz.uzkassa.smartpossupply.report.engine.ReportException;
import uz.uzkassa.smartpossupply.report.engine.ReportSecurityContext;
import uz.uzkassa.smartpossupply.service.report.ReportPreviewService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.cabinet.contract.ReportPreviewApi;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CabinetReportPreviewResource implements ReportPreviewApi {

    private static final long ASYNC_TIMEOUT_GRACE_MS = 1_000L;

    private final ReportPreviewService reportPreviewService;
    private final ApplicationProperties applicationProperties;

    @Override
    public WebAsyncTask<ResponseEntity<ReportPreviewResponse>> previewReport(ReportPreviewRequest request) {
        UUID executionId = UUID.randomUUID();
        ReportSecurityContext securityContext = new ReportSecurityContext(
                SecurityUtils.getCurrentUserId(),
                SecurityUtils.getCurrentCompanyId(),
                resolveLanguage()
        );
        long asyncTimeout = applicationProperties.getReportEngine().getStatementTimeoutMs()
                + ASYNC_TIMEOUT_GRACE_MS;
        WebAsyncTask<ResponseEntity<ReportPreviewResponse>> task = new WebAsyncTask<>(
                asyncTimeout,
                "taskExecutor",
                () -> ResponseEntity.ok(reportPreviewService.preview(executionId, request, securityContext))
        );
        task.onTimeout(() -> {
            reportPreviewService.cancel(executionId);
            throw ReportException.limit(
                    ReportErrorCode.STATEMENT_TIMEOUT,
                    "Время выполнения отчёта превысило допустимый лимит"
            );
        });
        task.onCompletion(() -> reportPreviewService.cancel(executionId));
        return task;
    }

    private LanguageEnum resolveLanguage() {
        HttpServletRequest request = SecurityUtils.getCurrentRequest();
        String language = request == null ? null : request.getHeader(AuthConstants.LANGUAGE);
        if (StringUtils.isNotBlank(language)) {
            return LanguageEnum.getByCode(language);
        }
        return LanguageEnum.getByCode(SecurityUtils.getLocaleCode());
    }
}
