package uz.uzkassa.smartpossupply.dto.report.preview;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Schema(description = "Sorting by a stable field key.")
public record ReportPreviewSort(
        @NotBlank
        @Schema(example = "sales_amount") String fieldKey,
        @NotNull ReportSortDirection direction
) implements Serializable {
}
