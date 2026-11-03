package uz.uzkassa.smartpossupply.dto.report.metadata;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(description = "Public limits used by the Frontend for an early warning. The server remains authoritative.")
public record ReportLimitsMetadata(
        @Schema(example = "50000") long maxCells,
        @Schema(example = "100000") long maxRows,
        @Schema(example = "731") long maxPeriodDays
) implements Serializable {

    public ReportLimitsMetadata(long maxCells, long maxRows) {
        this(maxCells, maxRows, 731);
    }
}
