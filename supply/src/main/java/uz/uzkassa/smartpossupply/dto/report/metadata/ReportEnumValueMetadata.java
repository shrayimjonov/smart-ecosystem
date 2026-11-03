package uz.uzkassa.smartpossupply.dto.report.metadata;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(description = "Stable value and its localized user-facing name.")
public record ReportEnumValueMetadata(
        @Schema(example = "COMPLETED") String value,
        @Schema(example = "Завершён") String name
) implements Serializable {
}
