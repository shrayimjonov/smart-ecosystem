package uz.uzkassa.smartpossupply.report.engine;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewRequest;
import uz.uzkassa.smartpossupply.report.dataset.ReportDatasetCatalog;

@Component
public class ReportQueryEngine {

    private final ReportSpecificationValidator validator;
    private final ReportSqlBuilder sqlBuilder;

    public ReportQueryEngine(
            ReportDatasetCatalog datasetCatalog,
            ApplicationProperties applicationProperties
    ) {
        ApplicationProperties.ReportEngineConfig config = applicationProperties.getReportEngine();
        validateConfiguration(config);
        this.validator = new ReportSpecificationValidator(datasetCatalog, config);
        this.sqlBuilder = new ReportSqlBuilder(config);
    }

    public ReportQueryPlan build(ReportPreviewRequest request, ReportSecurityContext securityContext) {
        return sqlBuilder.build(validator.validate(request, securityContext));
    }

    private void validateConfiguration(ApplicationProperties.ReportEngineConfig config) {
        if (config.getMaxCells() <= 0
                || config.getMaxRows() <= 0
                || config.getMaxRows() == Long.MAX_VALUE
                || config.getMaxPeriodDays() <= 0
                || config.getStatementTimeoutMs() <= 0
                || config.getMaxConcurrentPerCompany() <= 0) {
            throw new IllegalStateException("Report engine limits must be positive");
        }
    }
}
