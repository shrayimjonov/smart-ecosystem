package uz.uzkassa.smartpossupply.dto.report.metadata;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(description = "Date grouping option and the stable field key used in preview specifications.")
public record ReportDateGranularityMetadata(
        @Schema(example = "document_month") String key,
        ReportDateGranularity granularity,
        @Schema(example = "Месяц") String name,
        @Schema(example = "yyyy-MM") String format
) implements Serializable {
}
