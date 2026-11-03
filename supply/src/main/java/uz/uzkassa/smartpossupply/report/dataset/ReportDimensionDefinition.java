package uz.uzkassa.smartpossupply.report.dataset;

import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDataType;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public record ReportDimensionDefinition(
        String key,
        String expression,
        List<String> groupByExpressions,
        Set<String> requiredJoins,
        boolean sortable,
        ReportDataType dataType,
        Set<String> enumValues
) {

    private static final Pattern SAFE_KEY = Pattern.compile("[a-z][a-z0-9_]*");

    public ReportDimensionDefinition(
            String key,
            String expression,
            List<String> groupByExpressions,
            Set<String> requiredJoins,
            boolean sortable
    ) {
        this(key, expression, groupByExpressions, requiredJoins, sortable, ReportDataType.STRING, Set.of());
    }

    public ReportDimensionDefinition {
        key = requireText(key, "key");
        if (!SAFE_KEY.matcher(key).matches()) {
            throw new IllegalArgumentException("Unsafe dimension key: " + key);
        }
        expression = requireText(expression, "expression");
        groupByExpressions = groupByExpressions == null || groupByExpressions.isEmpty()
                ? List.of(expression)
                : List.copyOf(groupByExpressions);
        requiredJoins = requiredJoins == null ? Set.of() : Set.copyOf(requiredJoins);
        dataType = Objects.requireNonNull(dataType, "dataType must not be null");
        enumValues = enumValues == null ? Set.of() : Set.copyOf(enumValues);
        if (dataType == ReportDataType.ENUM && enumValues.isEmpty()) {
            throw new IllegalArgumentException("Enum dimension must define values: " + key);
        }
        if (dataType != ReportDataType.ENUM && !enumValues.isEmpty()) {
            throw new IllegalArgumentException("Only enum dimensions may define enum values: " + key);
        }
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return Objects.requireNonNull(value);
    }
}
