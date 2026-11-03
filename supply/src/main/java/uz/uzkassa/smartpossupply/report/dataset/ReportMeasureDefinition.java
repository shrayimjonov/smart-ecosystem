package uz.uzkassa.smartpossupply.report.dataset;

import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDataType;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public record ReportMeasureDefinition(
        String key,
        String expression,
        Set<String> requiredJoins,
        boolean additive,
        boolean sortable,
        boolean available,
        String unavailabilityReasonCode,
        ReportDataType dataType
) {

    private static final Pattern SAFE_KEY = Pattern.compile("[a-z][a-z0-9_]*");

    public ReportMeasureDefinition(
            String key,
            String expression,
            Set<String> requiredJoins,
            boolean additive,
            boolean sortable,
            boolean available,
            String unavailabilityReasonCode
    ) {
        this(
                key,
                expression,
                requiredJoins,
                additive,
                sortable,
                available,
                unavailabilityReasonCode,
                ReportDataType.DECIMAL
        );
    }

    public ReportMeasureDefinition {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
        if (!SAFE_KEY.matcher(key).matches()) {
            throw new IllegalArgumentException("Unsafe measure key: " + key);
        }
        if (available && (expression == null || expression.isBlank())) {
            throw new IllegalArgumentException("Available measure must define an expression: " + key);
        }
        if (!available && (unavailabilityReasonCode == null || unavailabilityReasonCode.isBlank())) {
            throw new IllegalArgumentException("Unavailable measure must define a reason code: " + key);
        }
        requiredJoins = requiredJoins == null ? Set.of() : Set.copyOf(requiredJoins);
        dataType = Objects.requireNonNull(dataType, "dataType must not be null");
        if (available) {
            unavailabilityReasonCode = null;
        }
    }
}
