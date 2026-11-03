package uz.uzkassa.smartpossupply.dto.report.preview;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Schema(description = "Totals calculated from source rows, never by summing already aggregated cells.")
public record ReportPreviewTotals(
        @Schema(description = "Grand-total measure values keyed by stable measure key.")
        Map<String, JsonNode> grandTotal,
        @Schema(description = "Subtotals keyed by selected row dimensions and measure keys.")
        List<Map<String, JsonNode>> rowTotals,
        @Schema(description = "Subtotals keyed by selected column dimensions and measure keys.")
        List<Map<String, JsonNode>> columnTotals
) implements Serializable {

    public ReportPreviewTotals {
        grandTotal = copyMap(grandTotal);
        rowTotals = copyRows(rowTotals);
        columnTotals = copyRows(columnTotals);
    }

    private static Map<String, JsonNode> copyMap(Map<String, JsonNode> values) {
        return values == null ? Map.of() : Map.copyOf(values);
    }

    private static List<Map<String, JsonNode>> copyRows(List<Map<String, JsonNode>> rows) {
        return rows == null ? List.of() : rows.stream().map(ReportPreviewTotals::copyMap).toList();
    }
}
