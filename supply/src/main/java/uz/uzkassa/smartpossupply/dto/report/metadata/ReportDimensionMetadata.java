package uz.uzkassa.smartpossupply.dto.report.metadata;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

@Schema(description = "Dimension available in report rows, columns or filters.")
public record ReportDimensionMetadata(
        @Schema(example = "customer_region") String key,
        @Schema(example = "Регион торговой точки") String name,
        ReportDataType dataType,
        ReportValueFormat format,
        List<ReportBuilderZone> allowedZones,
        @Schema(description = "True when the field may be used for sorting.") boolean sortable,
        List<ReportDateGranularityMetadata> granularities,
        List<ReportEnumValueMetadata> enumValues
) implements Serializable {

    public ReportDimensionMetadata {
        allowedZones = copyOf(allowedZones);
        granularities = copyOf(granularities);
        enumValues = copyOf(enumValues);
    }

    private static <T> List<T> copyOf(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
