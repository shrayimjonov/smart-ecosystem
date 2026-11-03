package uz.uzkassa.smartpossupply.dto.report.metadata;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "Constructor area that accepts the field.")
public enum ReportBuilderZone {
    ROWS("rows"),
    COLUMNS("columns"),
    FILTERS("filters"),
    MEASURES("measures");

    private final String value;

    ReportBuilderZone(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }
}
