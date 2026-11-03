package uz.uzkassa.smartpossupply.dto.report.preview;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;

@Schema(description = "Absolute date range of a preview request.")
public record ReportPreviewPeriod(
        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @Schema(type = "string", format = "date", example = "2026-09-01") LocalDate from,
        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @Schema(type = "string", format = "date", example = "2026-09-30") LocalDate to
) implements Serializable {

    @JsonIgnore
    @AssertTrue(message = "Period 'from' must not be after 'to'")
    public boolean isRangeValid() {
        return from == null || to == null || !from.isAfter(to);
    }
}
