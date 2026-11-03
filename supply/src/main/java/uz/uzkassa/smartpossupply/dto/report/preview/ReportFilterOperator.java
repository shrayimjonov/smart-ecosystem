package uz.uzkassa.smartpossupply.dto.report.preview;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, description = "Filter operator supported by a preview specification.")
public enum ReportFilterOperator {
    EQ("eq"),
    NE("ne"),
    GT("gt"),
    GTE("gte"),
    LT("lt"),
    LTE("lte"),
    IN("in"),
    NIN("nin"),
    CONTAINS("contains"),
    STARTS_WITH("startsWith"),
    IS_NULL("isNull"),
    NOT_NULL("notNull"),
    BETWEEN("between");

    private final String value;

    ReportFilterOperator(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }
}
