package uz.uzkassa.smartpossupply.report.dataset;

import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDataType;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDatasetType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record ReportDatasetDefinition(
        ReportDatasetType type,
        String baseFrom,
        String companyPredicate,
        String periodExpression,
        List<String> requiredPredicates,
        Map<String, String> defaultFilters,
        Map<String, ReportJoinDefinition> joins,
        Map<String, ReportDimensionDefinition> dimensions,
        Map<String, ReportMeasureDefinition> measures,
        Set<String> sourceTables
) {

    public ReportDatasetDefinition {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        baseFrom = requireText(baseFrom, "baseFrom");
        companyPredicate = requireText(companyPredicate, "companyPredicate");
        periodExpression = requireText(periodExpression, "periodExpression");
        requiredPredicates = List.copyOf(requiredPredicates);
        defaultFilters = immutableMap(defaultFilters);
        joins = immutableMap(joins);
        dimensions = immutableMap(dimensions);
        measures = immutableMap(measures);
        sourceTables = Set.copyOf(sourceTables);

        validateMapKeys(joins);
        validateMapKeys(dimensions);
        validateMapKeys(measures);
        Set<String> duplicateFieldKeys = new LinkedHashSet<>(dimensions.keySet());
        duplicateFieldKeys.retainAll(measures.keySet());
        if (!duplicateFieldKeys.isEmpty()) {
            throw new IllegalArgumentException("Dimension and measure keys overlap: " + duplicateFieldKeys);
        }
        for (String key : defaultFilters.keySet()) {
            if (!dimensions.containsKey(key)) {
                throw new IllegalArgumentException("Default filter references unknown dimension: " + key);
            }
            ReportDimensionDefinition dimension = dimensions.get(key);
            if (dimension.dataType() == ReportDataType.ENUM
                    && !dimension.enumValues().contains(defaultFilters.get(key))) {
                throw new IllegalArgumentException("Default filter contains unknown enum value: " + key);
            }
        }
        for (ReportDimensionDefinition field : dimensions.values()) {
            validateJoins(field.key(), field.requiredJoins(), joins);
        }
        for (ReportMeasureDefinition field : measures.values()) {
            validateJoins(field.key(), field.requiredJoins(), joins);
        }
        for (ReportJoinDefinition join : joins.values()) {
            validateJoins(join.key(), join.dependencies(), joins);
            validateJoinCycles(join.key(), joins, new LinkedHashSet<>());
        }
    }

    public ReportDimensionDefinition dimension(String key) {
        ReportDimensionDefinition definition = dimensions.get(key);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown dimension: " + key);
        }
        return definition;
    }

    public ReportMeasureDefinition measure(String key) {
        ReportMeasureDefinition definition = measures.get(key);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown measure: " + key);
        }
        return definition;
    }

    public List<ReportJoinDefinition> requiredJoinsFor(Set<String> dimensionKeys, Set<String> measureKeys) {
        LinkedHashSet<String> required = new LinkedHashSet<>();
        dimensionKeys.forEach(key -> collectJoinKeys(dimension(key).requiredJoins(), required));
        measureKeys.forEach(key -> collectJoinKeys(measure(key).requiredJoins(), required));
        return required.stream().map(joins::get).toList();
    }

    private void collectJoinKeys(Set<String> keys, LinkedHashSet<String> target) {
        for (String key : keys) {
            ReportJoinDefinition join = joins.get(key);
            for (String dependency : join.dependencies()) {
                collectJoinKeys(Set.of(dependency), target);
            }
            target.add(key);
        }
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }

    private static <T> Map<String, T> immutableMap(Map<String, T> source) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(source));
    }

    private static void validateMapKeys(Map<String, ?> values) {
        List<String> invalidKeys = new ArrayList<>();
        values.forEach((key, value) -> {
            String declaredKey = switch (value) {
                case ReportJoinDefinition join -> join.key();
                case ReportDimensionDefinition dimension -> dimension.key();
                case ReportMeasureDefinition measure -> measure.key();
                default -> key;
            };
            if (!key.equals(declaredKey)) {
                invalidKeys.add(key);
            }
        });
        if (!invalidKeys.isEmpty()) {
            throw new IllegalArgumentException("Map keys do not match definition keys: " + invalidKeys);
        }
    }

    private static void validateJoins(
            String fieldKey,
            Set<String> requiredJoins,
            Map<String, ReportJoinDefinition> joins
    ) {
        requiredJoins.forEach(join -> {
            if (!joins.containsKey(join)) {
                throw new IllegalArgumentException(fieldKey + " references unknown join: " + join);
            }
        });
    }

    private static void validateJoinCycles(
            String joinKey,
            Map<String, ReportJoinDefinition> joins,
            Set<String> path
    ) {
        if (!path.add(joinKey)) {
            throw new IllegalArgumentException("Cyclic report join dependency: " + path + " -> " + joinKey);
        }
        joins.get(joinKey).dependencies().forEach(dependency ->
                validateJoinCycles(dependency, joins, new LinkedHashSet<>(path))
        );
    }
}
