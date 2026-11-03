package uz.uzkassa.smartpossupply.report.engine;

import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDataType;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportFilterOperator;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewRequest;
import uz.uzkassa.smartpossupply.report.dataset.ReportDatasetDefinition;

import java.util.List;

record ValidatedReportSpecification(
        ReportPreviewRequest request,
        ReportSecurityContext securityContext,
        ReportDatasetDefinition dataset,
        List<ValidatedFilter> filters
) {
}

record ValidatedFilter(
        String fieldKey,
        ReportFilterOperator operator,
        ReportDataType dataType,
        boolean measure,
        Object value
) {
}
