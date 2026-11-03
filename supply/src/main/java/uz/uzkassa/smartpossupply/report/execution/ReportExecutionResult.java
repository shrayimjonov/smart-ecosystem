package uz.uzkassa.smartpossupply.report.execution;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record ReportExecutionResult(
        List<Map<String, Object>> rows,
        Map<String, Object> grandTotal,
        List<Map<String, Object>> rowTotals,
        List<Map<String, Object>> columnTotals
) {

    public ReportExecutionResult {
        rows = copyRows(rows);
        grandTotal = copyRow(grandTotal);
        rowTotals = copyRows(rowTotals);
        columnTotals = copyRows(columnTotals);
    }

    private static List<Map<String, Object>> copyRows(List<Map<String, Object>> rows) {
        return rows == null ? List.of() : rows.stream().map(ReportExecutionResult::copyRow).toList();
    }

    private static Map<String, Object> copyRow(Map<String, Object> row) {
        return row == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(row));
    }
}
