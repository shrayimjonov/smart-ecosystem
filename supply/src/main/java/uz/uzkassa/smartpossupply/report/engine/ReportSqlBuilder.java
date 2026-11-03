package uz.uzkassa.smartpossupply.report.engine;

import uz.uzkassa.smartpossupply.config.ApplicationProperties;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewRequest;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewSort;
import uz.uzkassa.smartpossupply.report.dataset.ReportDatasetDefinition;
import uz.uzkassa.smartpossupply.report.dataset.ReportDimensionDefinition;
import uz.uzkassa.smartpossupply.report.dataset.ReportJoinDefinition;
import uz.uzkassa.smartpossupply.report.dataset.ReportMeasureDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class ReportSqlBuilder {

    private final long maxRows;
    private final long maxCells;
    private final int statementTimeoutMs;

    ReportSqlBuilder(ApplicationProperties.ReportEngineConfig config) {
        this.maxRows = config.getMaxRows();
        this.maxCells = config.getMaxCells();
        this.statementTimeoutMs = config.getStatementTimeoutMs();
    }

    ReportQueryPlan build(ValidatedReportSpecification specification) {
        ReportPreviewRequest request = specification.request();
        ReportDatasetDefinition dataset = specification.dataset();
        List<String> dimensions = concat(request.rows(), request.columns());
        int outputColumnCount = dimensions.size() + request.measures().size();
        long resultRowLimit = Math.min(maxRows, maxCells / outputColumnCount);
        if (resultRowLimit <= 0) {
            throw ReportException.limit(
                    ReportErrorCode.MAX_CELLS_EXCEEDED,
                    "Выбранные поля превышают лимит " + maxCells + " ячеек"
            );
        }

        Map<String, Object> parameters = parameters(specification, resultRowLimit);
        FilterClauses filterClauses = filters(specification, parameters);

        ReportSqlQuery dataQuery = query(
                dataset,
                dimensions,
                request.measures(),
                filterClauses,
                request.sort(),
                parameters
        );
        ReportSqlQuery grandTotalQuery = query(
                dataset,
                List.of(),
                request.measures(),
                filterClauses,
                List.of(),
                parameters
        );
        ReportSqlQuery rowTotalsQuery = request.rows().isEmpty() ? null : query(
                dataset,
                request.rows(),
                request.measures(),
                filterClauses,
                List.of(),
                parameters
        );
        ReportSqlQuery columnTotalsQuery = request.columns().isEmpty() ? null : query(
                dataset,
                request.columns(),
                request.measures(),
                filterClauses,
                List.of(),
                parameters
        );

        return new ReportQueryPlan(
                dataset.type(),
                dimensions,
                request.measures(),
                dataQuery,
                grandTotalQuery,
                rowTotalsQuery,
                columnTotalsQuery,
                outputColumnCount,
                resultRowLimit,
                maxRows,
                maxCells,
                statementTimeoutMs
        );
    }

    private Map<String, Object> parameters(
            ValidatedReportSpecification specification,
            long resultRowLimit
    ) {
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("companyId", specification.securityContext().companyId());
        parameters.put("language", specification.securityContext().effectiveLanguage().name());
        parameters.put("periodFrom", specification.request().period().from());
        parameters.put("periodToExclusive", specification.request().period().to().plusDays(1));
        parameters.put("resultLimit", Math.addExact(resultRowLimit, 1));
        return parameters;
    }

    private FilterClauses filters(
            ValidatedReportSpecification specification,
            Map<String, Object> parameters
    ) {
        ReportDatasetDefinition dataset = specification.dataset();
        List<String> where = new ArrayList<>();
        List<String> having = new ArrayList<>();
        Set<String> filteredDimensions = new LinkedHashSet<>();
        Set<String> filteredMeasures = new LinkedHashSet<>();

        Set<String> explicitlyFiltered = new LinkedHashSet<>();
        specification.filters().forEach(filter -> explicitlyFiltered.add(filter.fieldKey()));
        dataset.defaultFilters().forEach((fieldKey, value) -> {
            if (!explicitlyFiltered.contains(fieldKey)) {
                ReportDimensionDefinition field = dataset.dimension(fieldKey);
                String parameter = "default_" + fieldKey;
                where.add(field.expression() + " = :" + parameter);
                parameters.put(parameter, value);
                filteredDimensions.add(fieldKey);
            }
        });

        for (int index = 0; index < specification.filters().size(); index++) {
            ValidatedFilter filter = specification.filters().get(index);
            String expression = filter.measure()
                    ? dataset.measure(filter.fieldKey()).expression()
                    : dataset.dimension(filter.fieldKey()).expression();
            String clause = filterClause(expression, filter, index, parameters);
            if (filter.measure()) {
                having.add(clause);
                filteredMeasures.add(filter.fieldKey());
            } else {
                where.add(clause);
                filteredDimensions.add(filter.fieldKey());
            }
        }
        return new FilterClauses(where, having, filteredDimensions, filteredMeasures);
    }

    private String filterClause(
            String expression,
            ValidatedFilter filter,
            int index,
            Map<String, Object> parameters
    ) {
        String parameter = "filter_" + index;
        return switch (filter.operator()) {
            case EQ -> comparison(expression, "=", parameter, filter.value(), parameters);
            case NE -> comparison(expression, "<>", parameter, filter.value(), parameters);
            case GT -> comparison(expression, ">", parameter, filter.value(), parameters);
            case GTE -> comparison(expression, ">=", parameter, filter.value(), parameters);
            case LT -> comparison(expression, "<", parameter, filter.value(), parameters);
            case LTE -> comparison(expression, "<=", parameter, filter.value(), parameters);
            case IN -> collectionComparison(expression, "IN", parameter, filter.value(), parameters);
            case NIN -> collectionComparison(expression, "NOT IN", parameter, filter.value(), parameters);
            case CONTAINS -> {
                parameters.put(parameter, filter.value());
                yield "POSITION(LOWER(CAST(:" + parameter + " AS text)) IN LOWER(CAST("
                        + expression + " AS text))) > 0";
            }
            case STARTS_WITH -> {
                parameters.put(parameter, filter.value());
                yield "LEFT(LOWER(CAST(" + expression + " AS text)), LENGTH(CAST(:" + parameter
                        + " AS text))) = LOWER(CAST(:" + parameter + " AS text))";
            }
            case IS_NULL -> expression + " IS NULL";
            case NOT_NULL -> expression + " IS NOT NULL";
            case BETWEEN -> {
                List<?> values = (List<?>) filter.value();
                parameters.put(parameter + "_from", values.get(0));
                parameters.put(parameter + "_to", values.get(1));
                yield expression + " BETWEEN :" + parameter + "_from AND :" + parameter + "_to";
            }
        };
    }

    private String comparison(
            String expression,
            String operator,
            String parameter,
            Object value,
            Map<String, Object> parameters
    ) {
        parameters.put(parameter, value);
        return expression + " " + operator + " :" + parameter;
    }

    private String collectionComparison(
            String expression,
            String operator,
            String parameter,
            Object value,
            Map<String, Object> parameters
    ) {
        parameters.put(parameter, value);
        return expression + " " + operator + " (:" + parameter + ")";
    }

    private ReportSqlQuery query(
            ReportDatasetDefinition dataset,
            List<String> dimensionKeys,
            List<String> measureKeys,
            FilterClauses filters,
            List<ReportPreviewSort> sort,
            Map<String, Object> parameters
    ) {
        List<String> select = new ArrayList<>();
        for (String key : dimensionKeys) {
            ReportDimensionDefinition dimension = dataset.dimension(key);
            select.add(dimension.expression() + " AS " + quoteIdentifier(key));
        }
        for (String key : measureKeys) {
            ReportMeasureDefinition measure = dataset.measure(key);
            select.add(measure.expression() + " AS " + quoteIdentifier(key));
        }

        Set<String> requiredDimensions = new LinkedHashSet<>(dimensionKeys);
        requiredDimensions.addAll(filters.dimensionKeys());
        Set<String> requiredMeasures = new LinkedHashSet<>(measureKeys);
        requiredMeasures.addAll(filters.measureKeys());

        StringBuilder sql = new StringBuilder("SELECT ")
                .append(String.join(", ", select))
                .append(" FROM ")
                .append(dataset.baseFrom());
        for (ReportJoinDefinition join : dataset.requiredJoinsFor(requiredDimensions, requiredMeasures)) {
            sql.append(' ').append(join.sql());
        }

        List<String> where = new ArrayList<>();
        where.add(dataset.companyPredicate());
        where.addAll(dataset.requiredPredicates());
        where.addAll(filters.where());
        appendPredicates(sql, " WHERE ", where);

        List<String> groupBy = dimensionKeys.stream()
                .map(dataset::dimension)
                .map(ReportDimensionDefinition::groupByExpressions)
                .flatMap(Collection::stream)
                .distinct()
                .toList();
        if (!groupBy.isEmpty()) {
            sql.append(" GROUP BY ").append(String.join(", ", groupBy));
        }
        appendPredicates(sql, " HAVING ", filters.having());

        if (!sort.isEmpty()) {
            List<String> ordering = sort.stream()
                    .map(item -> quoteIdentifier(item.fieldKey()) + " " + item.direction().name())
                    .toList();
            sql.append(" ORDER BY ").append(String.join(", ", ordering));
        }
        sql.append(" LIMIT :resultLimit");
        return new ReportSqlQuery(sql.toString(), parameters);
    }

    private void appendPredicates(StringBuilder sql, String prefix, List<String> predicates) {
        if (!predicates.isEmpty()) {
            sql.append(prefix)
                    .append(predicates.stream().map(value -> "(" + value + ")").collect(java.util.stream.Collectors.joining(" AND ")));
        }
    }

    private String quoteIdentifier(String key) {
        return '"' + key + '"';
    }

    private <T> List<T> concat(List<T> first, List<T> second) {
        List<T> result = new ArrayList<>(first.size() + second.size());
        result.addAll(first);
        result.addAll(second);
        return List.copyOf(result);
    }

    private record FilterClauses(
            List<String> where,
            List<String> having,
            Set<String> dimensionKeys,
            Set<String> measureKeys
    ) {
        private FilterClauses {
            where = List.copyOf(where);
            having = List.copyOf(having);
            dimensionKeys = Set.copyOf(dimensionKeys);
            measureKeys = Set.copyOf(measureKeys);
        }
    }
}
