package uz.uzkassa.smartpossupply.dto.report.preview;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.stream.StreamSupport;

@Schema(description = "A filter over a stable dimension or measure key.")
public record ReportPreviewFilter(
        @NotBlank
        @Schema(example = "document_status") String fieldKey,
        @NotNull ReportFilterOperator operator,
        @Schema(
                description = "A JSON scalar for comparison operators, a non-empty array for in/nin, "
                        + "two values for between, and no value for isNull/notNull.",
                nullable = true
        ) JsonNode value
) implements Serializable {

    @JsonIgnore
    @AssertTrue(message = "Filter value does not match the selected operator")
    public boolean isValueValid() {
        if (operator == null) {
            return true;
        }

        return switch (operator) {
            case IS_NULL, NOT_NULL -> value == null || value.isNull();
            case IN, NIN -> isScalarArray(value, false);
            case BETWEEN -> isScalarArray(value, true);
            case CONTAINS, STARTS_WITH -> value != null && value.isTextual();
            default -> value != null && value.isValueNode() && !value.isNull();
        };
    }

    private static boolean isScalarArray(JsonNode node, boolean exactlyTwoValues) {
        if (node == null || !node.isArray() || node.isEmpty()) {
            return false;
        }
        if (exactlyTwoValues && node.size() != 2) {
            return false;
        }
        return StreamSupport.stream(node.spliterator(), false)
                .allMatch(item -> item.isValueNode() && !item.isNull());
    }
}
