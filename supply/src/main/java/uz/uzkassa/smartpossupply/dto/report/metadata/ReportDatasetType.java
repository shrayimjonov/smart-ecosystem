package uz.uzkassa.smartpossupply.dto.report.metadata;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "Stable dataset discriminator used to select a report strategy.")
public enum ReportDatasetType {
    SALES("sales");

    private final String value;

    ReportDatasetType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }
}
