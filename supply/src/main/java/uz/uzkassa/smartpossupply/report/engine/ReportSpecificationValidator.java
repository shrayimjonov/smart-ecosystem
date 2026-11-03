package uz.uzkassa.smartpossupply.report.engine;

import com.fasterxml.jackson.databind.JsonNode;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDataType;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportFilterOperator;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewFilter;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewRequest;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewSort;
import uz.uzkassa.smartpossupply.report.dataset.ReportDatasetCatalog;
import uz.uzkassa.smartpossupply.report.dataset.ReportDatasetDefinition;
import uz.uzkassa.smartpossupply.report.dataset.ReportDimensionDefinition;
import uz.uzkassa.smartpossupply.report.dataset.ReportMeasureDefinition;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class ReportSpecificationValidator {

    private static final Set<ReportFilterOperator> STRING_OPERATORS = Set.of(
            ReportFilterOperator.EQ,
            ReportFilterOperator.NE,
            ReportFilterOperator.IN,
            ReportFilterOperator.NIN,
            ReportFilterOperator.CONTAINS,
            ReportFilterOperator.STARTS_WITH,
            ReportFilterOperator.IS_NULL,
            ReportFilterOperator.NOT_NULL
    );
    private static final Set<ReportFilterOperator> COMPARABLE_OPERATORS = Set.of(
            ReportFilterOperator.EQ,
            ReportFilterOperator.NE,
            ReportFilterOperator.GT,
            ReportFilterOperator.GTE,
            ReportFilterOperator.LT,
            ReportFilterOperator.LTE,
            ReportFilterOperator.IN,
            ReportFilterOperator.NIN,
            ReportFilterOperator.IS_NULL,
            ReportFilterOperator.NOT_NULL,
            ReportFilterOperator.BETWEEN
    );
    private static final Set<ReportFilterOperator> EXACT_OPERATORS = Set.of(
            ReportFilterOperator.EQ,
            ReportFilterOperator.NE,
            ReportFilterOperator.IN,
            ReportFilterOperator.NIN,
            ReportFilterOperator.IS_NULL,
            ReportFilterOperator.NOT_NULL
    );

    private final ReportDatasetCatalog datasetCatalog;
    private final long maxPeriodDays;

    ReportSpecificationValidator(
            ReportDatasetCatalog datasetCatalog,
            ApplicationProperties.ReportEngineConfig config
    ) {
        this.datasetCatalog = datasetCatalog;
        this.maxPeriodDays = config.getMaxPeriodDays();
    }

    ValidatedReportSpecification validate(
            ReportPreviewRequest request,
            ReportSecurityContext securityContext
    ) {
        validateSecurityContext(securityContext);
        if (request == null) {
            throw validation(ReportErrorCode.INVALID_SPECIFICATION, "Спецификация отчёта обязательна");
        }

        ReportDatasetDefinition dataset = datasetCatalog.all().get(request.datasetType());
        if (dataset == null) {
            throw validation(ReportErrorCode.UNKNOWN_DATASET, "Неизвестный датасет: " + request.datasetType());
        }

        List<String> rows = requireList(request.rows(), "rows");
        List<String> columns = requireList(request.columns(), "columns");
        List<String> measures = requireList(request.measures(), "measures");
        List<ReportPreviewFilter> filters = requireList(request.filters(), "filters");
        List<ReportPreviewSort> sorts = requireList(request.sort(), "sort");
        if (measures.isEmpty()) {
            throw validation(ReportErrorCode.INVALID_SPECIFICATION, "Нужно выбрать хотя бы одну меру");
        }

        validateDimensions(dataset, rows, "rows");
        validateDimensions(dataset, columns, "columns");
        validateMeasures(dataset, measures);
        validateSelectedDuplicates(rows, columns, measures);
        List<ValidatedFilter> validatedFilters = validateFilters(dataset, filters);
        validateSort(dataset, sorts, rows, columns, measures);
        validatePeriod(request);
        validateMinimumResultSize(rows.size() + columns.size() + measures.size());

        return new ValidatedReportSpecification(request, securityContext, dataset, validatedFilters);
    }

    private void validateSecurityContext(ReportSecurityContext securityContext) {
        if (securityContext == null
                || securityContext.userId() == null
                || securityContext.companyId() == null) {
            throw ReportException.forbidden(
                    ReportErrorCode.SECURITY_CONTEXT_REQUIRED,
                    "Отсутствует обязательный контекст пользователя или компании"
            );
        }
    }

    private void validateDimensions(
            ReportDatasetDefinition dataset,
            List<String> keys,
            String zone
    ) {
        for (String key : keys) {
            requireKey(key, zone);
            if (dataset.dimensions().containsKey(key)) {
                continue;
            }
            if (dataset.measures().containsKey(key)) {
                throw validation(
                        ReportErrorCode.INVALID_ZONE,
                        "Мера " + key + " не может использоваться в зоне " + zone
                );
            }
            throw validation(ReportErrorCode.UNKNOWN_FIELD, "Неизвестное измерение: " + key);
        }
    }

    private void validateMeasures(ReportDatasetDefinition dataset, List<String> keys) {
        for (String key : keys) {
            requireKey(key, "measures");
            ReportMeasureDefinition measure = dataset.measures().get(key);
            if (measure == null) {
                if (dataset.dimensions().containsKey(key)) {
                    throw validation(
                            ReportErrorCode.INVALID_ZONE,
                            "Измерение " + key + " не может использоваться как мера"
                    );
                }
                throw validation(ReportErrorCode.UNKNOWN_FIELD, "Неизвестная мера: " + key);
            }
            if (!measure.available()) {
                throw validation(ReportErrorCode.INVALID_ZONE, "Мера пока недоступна: " + key);
            }
        }
    }

    private void validateSelectedDuplicates(
            List<String> rows,
            List<String> columns,
            List<String> measures
    ) {
        Set<String> selected = new HashSet<>();
        for (String key : concat(rows, columns, measures)) {
            if (!selected.add(key)) {
                throw validation(ReportErrorCode.DUPLICATE_FIELD, "Поле выбрано повторно: " + key);
            }
        }
    }

    private List<ValidatedFilter> validateFilters(
            ReportDatasetDefinition dataset,
            List<ReportPreviewFilter> filters
    ) {
        List<ValidatedFilter> result = new ArrayList<>(filters.size());
        for (ReportPreviewFilter filter : filters) {
            if (filter == null || filter.fieldKey() == null || filter.fieldKey().isBlank()) {
                throw validation(ReportErrorCode.INVALID_SPECIFICATION, "Ключ поля фильтра обязателен");
            }
            if ("company_id".equals(filter.fieldKey())) {
                throw ReportException.forbidden(
                        ReportErrorCode.COMPANY_FILTER_FORBIDDEN,
                        "Компания определяется только из security-контекста"
                );
            }
            if (filter.operator() == null) {
                throw validation(ReportErrorCode.INVALID_FILTER_OPERATOR, "Оператор фильтра обязателен");
            }

            ReportDimensionDefinition dimension = dataset.dimensions().get(filter.fieldKey());
            ReportMeasureDefinition measure = dataset.measures().get(filter.fieldKey());
            if (dimension == null && measure == null) {
                throw validation(ReportErrorCode.UNKNOWN_FIELD, "Неизвестное поле фильтра: " + filter.fieldKey());
            }
            if (measure != null && !measure.available()) {
                throw validation(ReportErrorCode.INVALID_ZONE, "Мера пока недоступна: " + filter.fieldKey());
            }

            ReportDataType dataType = dimension != null ? dimension.dataType() : measure.dataType();
            validateOperator(filter, dataType);
            Object value = normalizeValue(filter, dataType);
            if (dimension != null
                    && dataType == ReportDataType.ENUM
                    && filter.operator() != ReportFilterOperator.IS_NULL
                    && filter.operator() != ReportFilterOperator.NOT_NULL) {
                validateEnumValue(filter.fieldKey(), dimension.enumValues(), value);
            }
            result.add(new ValidatedFilter(
                    filter.fieldKey(),
                    filter.operator(),
                    dataType,
                    measure != null,
                    value
            ));
        }
        return List.copyOf(result);
    }

    private void validateOperator(ReportPreviewFilter filter, ReportDataType dataType) {
        Set<ReportFilterOperator> supported = switch (dataType) {
            case STRING -> STRING_OPERATORS;
            case DATE, DECIMAL, INTEGER -> COMPARABLE_OPERATORS;
            case ENUM, BOOLEAN -> EXACT_OPERATORS;
        };
        if (!supported.contains(filter.operator())) {
            throw validation(
                    ReportErrorCode.INVALID_FILTER_OPERATOR,
                    "Оператор " + filter.operator().value() + " не поддерживается для " + filter.fieldKey()
            );
        }
    }

    private Object normalizeValue(ReportPreviewFilter filter, ReportDataType dataType) {
        JsonNode value = filter.value();
        return switch (filter.operator()) {
            case IS_NULL, NOT_NULL -> {
                if (value != null && !value.isNull()) {
                    throw invalidValue(filter.fieldKey());
                }
                yield null;
            }
            case IN, NIN -> normalizeArray(filter.fieldKey(), value, dataType, false);
            case BETWEEN -> normalizeArray(filter.fieldKey(), value, dataType, true);
            default -> normalizeScalar(filter.fieldKey(), value, dataType);
        };
    }

    private List<Object> normalizeArray(
            String fieldKey,
            JsonNode node,
            ReportDataType dataType,
            boolean exactlyTwo
    ) {
        if (node == null || !node.isArray() || node.isEmpty() || (exactlyTwo && node.size() != 2)) {
            throw invalidValue(fieldKey);
        }
        List<Object> values = new ArrayList<>(node.size());
        node.forEach(item -> values.add(normalizeScalar(fieldKey, item, dataType)));
        return List.copyOf(values);
    }

    private Object normalizeScalar(String fieldKey, JsonNode node, ReportDataType dataType) {
        if (node == null || node.isNull() || !node.isValueNode()) {
            throw invalidValue(fieldKey);
        }
        try {
            return switch (dataType) {
                case STRING, ENUM -> {
                    if (!node.isTextual()) {
                        throw invalidValue(fieldKey);
                    }
                    yield node.textValue();
                }
                case BOOLEAN -> {
                    if (!node.isBoolean()) {
                        throw invalidValue(fieldKey);
                    }
                    yield node.booleanValue();
                }
                case DATE -> normalizeDate(fieldKey, node);
                case DECIMAL -> {
                    if (!node.isNumber()) {
                        throw invalidValue(fieldKey);
                    }
                    yield new BigDecimal(node.asText());
                }
                case INTEGER -> {
                    if (!node.isIntegralNumber() || !node.canConvertToLong()) {
                        throw invalidValue(fieldKey);
                    }
                    yield node.longValue();
                }
            };
        } catch (DateTimeException | NumberFormatException exception) {
            throw invalidValue(fieldKey);
        }
    }

    private Object normalizeDate(String fieldKey, JsonNode node) {
        if (!node.isTextual()) {
            throw invalidValue(fieldKey);
        }
        String value = node.textValue();
        return switch (fieldKey) {
            case "document_week" -> {
                if (!value.matches("\\d{4}-W(0[1-9]|[1-4]\\d|5[0-3])")) {
                    throw invalidValue(fieldKey);
                }
                yield value;
            }
            case "document_month" -> {
                YearMonth.parse(value);
                yield value;
            }
            case "document_quarter" -> {
                if (!value.matches("\\d{4}-Q[1-4]")) {
                    throw invalidValue(fieldKey);
                }
                yield value;
            }
            case "document_year" -> {
                Year.parse(value);
                yield value;
            }
            default -> LocalDate.parse(value);
        };
    }

    private void validateEnumValue(String fieldKey, Set<String> allowed, Object value) {
        List<?> values = value instanceof List<?> list ? list : List.of(value);
        for (Object item : values) {
            if (!allowed.contains(item)) {
                throw validation(
                        ReportErrorCode.INVALID_ENUM_VALUE,
                        "Недопустимое значение " + item + " для " + fieldKey
                );
            }
        }
    }

    private void validateSort(
            ReportDatasetDefinition dataset,
            List<ReportPreviewSort> sorts,
            List<String> rows,
            List<String> columns,
            List<String> measures
    ) {
        Set<String> outputFields = new HashSet<>(concat(rows, columns, measures));
        Set<String> sorted = new HashSet<>();
        for (ReportPreviewSort sort : sorts) {
            if (sort == null || sort.fieldKey() == null || sort.fieldKey().isBlank() || sort.direction() == null) {
                throw validation(ReportErrorCode.INVALID_SPECIFICATION, "Сортировка заполнена некорректно");
            }
            if (!sorted.add(sort.fieldKey())) {
                throw validation(ReportErrorCode.DUPLICATE_FIELD, "Сортировка повторяется: " + sort.fieldKey());
            }
            if (!outputFields.contains(sort.fieldKey())) {
                throw validation(
                        ReportErrorCode.INVALID_ZONE,
                        "Сортировать можно только по выбранному полю: " + sort.fieldKey()
                );
            }
            boolean sortable = dataset.dimensions().containsKey(sort.fieldKey())
                    ? dataset.dimension(sort.fieldKey()).sortable()
                    : dataset.measure(sort.fieldKey()).sortable();
            if (!sortable) {
                throw validation(ReportErrorCode.UNSORTABLE_FIELD, "Поле не поддерживает сортировку: " + sort.fieldKey());
            }
        }
    }

    private void validatePeriod(ReportPreviewRequest request) {
        if (request.period() == null || request.period().from() == null || request.period().to() == null) {
            throw validation(ReportErrorCode.INVALID_PERIOD, "Период отчёта обязателен");
        }
        if (request.period().from().isAfter(request.period().to())) {
            throw validation(ReportErrorCode.INVALID_PERIOD, "Начало периода не может быть позже окончания");
        }
        if (request.period().to().equals(LocalDate.MAX)) {
            throw validation(ReportErrorCode.INVALID_PERIOD, "Конец периода находится вне поддерживаемого диапазона");
        }
        long periodDays = ChronoUnit.DAYS.between(request.period().from(), request.period().to()) + 1;
        if (periodDays > maxPeriodDays) {
            throw ReportException.limit(
                    ReportErrorCode.MAX_PERIOD_DAYS_EXCEEDED,
                    "Период отчёта превышает " + maxPeriodDays + " дней"
            );
        }
    }

    private void validateMinimumResultSize(int outputColumnCount) {
        if (outputColumnCount <= 0) {
            throw validation(ReportErrorCode.INVALID_SPECIFICATION, "Отчёт не содержит выходных полей");
        }
    }

    private <T> List<T> requireList(List<T> values, String field) {
        if (values == null) {
            throw validation(ReportErrorCode.INVALID_SPECIFICATION, "Поле " + field + " обязательно");
        }
        return values;
    }

    private void requireKey(String key, String zone) {
        if (key == null || key.isBlank()) {
            throw validation(ReportErrorCode.INVALID_SPECIFICATION, "Пустой ключ поля в зоне " + zone);
        }
    }

    @SafeVarargs
    private final <T> List<T> concat(List<T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<T> list : lists) {
            result.addAll(list);
        }
        return result;
    }

    private ReportException invalidValue(String fieldKey) {
        return validation(ReportErrorCode.INVALID_FILTER_VALUE, "Некорректное значение фильтра: " + fieldKey);
    }

    private ReportException validation(ReportErrorCode code, String message) {
        return ReportException.validation(code, message);
    }
}
