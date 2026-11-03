package uz.uzkassa.smartpossupply.dto.report.metadata;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

@Schema(description = "Metadata required to build a report constructor for one dataset.")
public record ReportDatasetMetadata(
        ReportDatasetType type,
        @Schema(example = "Продажи (отгрузки)") String name,
        List<ReportDimensionMetadata> dimensions,
        List<ReportMeasureMetadata> measures,
        ReportPeriodMetadata period,
        ReportLimitsMetadata limits
) implements Serializable {

    public ReportDatasetMetadata {
        dimensions = copyOf(dimensions);
        measures = copyOf(measures);
    }

    private static <T> List<T> copyOf(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
