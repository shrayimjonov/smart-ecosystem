package uz.uzkassa.smartpossupply.dto.report.preview;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "Sort direction.")
public enum ReportSortDirection {
    ASC("asc"),
    DESC("desc");

    private final String value;

    ReportSortDirection(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }
}
