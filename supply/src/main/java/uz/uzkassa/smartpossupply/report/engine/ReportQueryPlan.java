package uz.uzkassa.smartpossupply.report.engine;

import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDatasetType;

import java.util.List;

public record ReportQueryPlan(
        ReportDatasetType datasetType,
        List<String> dimensionKeys,
        List<String> measureKeys,
        ReportSqlQuery dataQuery,
        ReportSqlQuery grandTotalQuery,
        ReportSqlQuery rowTotalsQuery,
        ReportSqlQuery columnTotalsQuery,
        int outputColumnCount,
        long resultRowLimit,
        long maxRows,
        long maxCells,
        int statementTimeoutMs
) {

    public ReportQueryPlan {
        dimensionKeys = List.copyOf(dimensionKeys);
        measureKeys = List.copyOf(measureKeys);
    }

    public void validateResultSize(long rowCount) {
        if (rowCount > maxRows) {
            throw ReportException.limit(
                    ReportErrorCode.MAX_ROWS_EXCEEDED,
                    "Результат содержит больше " + maxRows + " строк"
            );
        }
        if (rowCount > maxCells / outputColumnCount) {
            throw ReportException.limit(
                    ReportErrorCode.MAX_CELLS_EXCEEDED,
                    "Результат содержит больше " + maxCells + " ячеек"
            );
        }
    }
}
