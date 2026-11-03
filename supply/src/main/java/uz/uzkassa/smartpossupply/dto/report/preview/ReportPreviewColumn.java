package uz.uzkassa.smartpossupply.dto.report.preview;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDataType;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportValueFormat;

import java.io.Serializable;

@Schema(description = "A column returned by a report preview.")
public record ReportPreviewColumn(
        @NotBlank @Schema(example = "customer_region") String key,
        @NotBlank @Schema(example = "Регион торговой точки") String name,
        @NotNull ReportDataType dataType,
        @NotNull ReportValueFormat format
) implements Serializable {
}
