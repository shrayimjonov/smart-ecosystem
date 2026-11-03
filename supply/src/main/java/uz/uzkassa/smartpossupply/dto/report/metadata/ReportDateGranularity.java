package uz.uzkassa.smartpossupply.dto.report.metadata;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "Supported grouping granularity for a date dimension.")
public enum ReportDateGranularity {
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
    QUARTER("quarter"),
    YEAR("year");

    private final String value;

    ReportDateGranularity(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }
}
