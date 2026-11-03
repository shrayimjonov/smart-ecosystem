package uz.uzkassa.smartpossupply.dto.report.preview;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDatasetType;

import java.io.Serializable;
import java.util.List;

@Schema(name = "ReportPreviewRequestV1", description = "Specification of a report preview.")
public record ReportPreviewRequest(
        @NotNull ReportDatasetType datasetType,
        @NotNull List<@NotBlank String> rows,
        @NotNull List<@NotBlank String> columns,
        @NotEmpty List<@NotBlank String> measures,
        @NotNull List<@Valid ReportPreviewFilter> filters,
        @NotNull List<@Valid ReportPreviewSort> sort,
        @NotNull @Valid ReportPreviewPeriod period
) implements Serializable {

    public ReportPreviewRequest {
        rows = copyOfNullable(rows);
        columns = copyOfNullable(columns);
        measures = copyOfNullable(measures);
        filters = copyOfNullable(filters);
        sort = copyOfNullable(sort);
    }

    private static <T> List<T> copyOfNullable(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }
}
