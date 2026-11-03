package uz.uzkassa.smartpossupply.dto.report.preview;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Schema(name = "ReportPreviewResponseV1", description = "A dataset-independent tabular report preview.")
public record ReportPreviewResponse(
        @NotEmpty List<@Valid ReportPreviewColumn> columns,
        @NotNull
        @Schema(description = "Rows keyed by stable column keys. Values are JSON scalars or null.")
        List<Map<String, JsonNode>> rows,
        @NotNull
        @Schema(type = "string", format = "date-time", example = "2026-10-07T05:00:00Z")
        Instant generatedAt,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(description = "Optional source-calculated totals. Added without changing existing response fields.", nullable = true)
        @Valid ReportPreviewTotals totals
) implements Serializable {

    /**
     * Keeps the original response constructor source-compatible for existing callers.
     */
    public ReportPreviewResponse(
            List<ReportPreviewColumn> columns,
            List<Map<String, JsonNode>> rows,
            Instant generatedAt
    ) {
        this(columns, rows, generatedAt, null);
    }
}
