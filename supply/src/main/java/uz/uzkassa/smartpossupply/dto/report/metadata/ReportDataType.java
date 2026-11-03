package uz.uzkassa.smartpossupply.dto.report.metadata;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "Logical type of a report field value.")
public enum ReportDataType {
    STRING("string"),
    BOOLEAN("boolean"),
    DATE("date"),
    DECIMAL("decimal"),
    INTEGER("integer"),
    ENUM("enum");

    private final String value;

    ReportDataType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }
}
