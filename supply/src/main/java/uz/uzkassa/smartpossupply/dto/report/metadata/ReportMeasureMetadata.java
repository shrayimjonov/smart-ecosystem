package uz.uzkassa.smartpossupply.dto.report.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

@Schema(description = "Measure available in the report constructor.")
public record ReportMeasureMetadata(
        @Schema(example = "sales_amount") String key,
        @Schema(example = "Сумма продаж") String name,
        ReportDataType dataType,
        ReportValueFormat format,
        List<ReportBuilderZone> allowedZones,
        boolean approximate,
        boolean unreliable,
        @Schema(description = "True when the measure may be used for sorting.") boolean sortable,
        @Schema(description = "False when the measure is advertised but cannot be requested yet.") boolean available,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(description = "Localized explanation shown when available is false.", nullable = true)
        String unavailabilityReason
) implements Serializable {

    /**
     * Keeps the original Java contract source-compatible. Existing measures are available by default.
     */
    public ReportMeasureMetadata(
            String key,
            String name,
            ReportDataType dataType,
            ReportValueFormat format,
            List<ReportBuilderZone> allowedZones,
            boolean approximate,
            boolean unreliable,
            boolean sortable
    ) {
        this(key, name, dataType, format, allowedZones, approximate, unreliable, sortable, true, null);
    }

    public ReportMeasureMetadata {
        allowedZones = allowedZones == null ? List.of() : List.copyOf(allowedZones);
        if (available) {
            unavailabilityReason = null;
        }
    }
}
