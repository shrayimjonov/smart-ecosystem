package uz.uzkassa.smartpossupply.dto.report.metadata;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "Presentation format hint. Raw values retain their JSON type.")
public enum ReportValueFormat {
    TEXT("text"),
    DATE("date"),
    ENUM("enum"),
    BOOLEAN("boolean"),
    MONEY("money"),
    QUANTITY("quantity"),
    COUNT("count"),
    PERCENT("percent");

    private final String value;

    ReportValueFormat(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }
}
