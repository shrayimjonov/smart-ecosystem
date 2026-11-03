package uz.uzkassa.smartpossupply.report.execution;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDataType;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDatasetMetadata;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDatasetType;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDimensionMetadata;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportMeasureMetadata;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportMetadataFixtures;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportValueFormat;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewColumn;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewRequest;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewResponse;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewTotals;
import uz.uzkassa.smartpossupply.enums.LanguageEnum;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReportPreviewResponseAssembler {

    public ReportPreviewResponse assemble(
            ReportPreviewRequest request,
            ReportExecutionResult result,
            LanguageEnum language
    ) {
        ReportDatasetMetadata metadata = metadata(request.datasetType(), language);
        Map<String, FieldMetadata> fields = fields(metadata);
        List<String> orderedKeys = orderedKeys(request);

        List<ReportPreviewColumn> responseColumns = orderedKeys.stream()
                .map(key -> column(key, requireField(fields, key)))
                .toList();
        List<Map<String, JsonNode>> rows = convertRows(result.rows(), orderedKeys, fields);
        ReportPreviewTotals totals = rows.isEmpty() ? null : new ReportPreviewTotals(
                convertRow(result.grandTotal(), request.measures(), fields),
                convertRows(result.rowTotals(), concat(request.rows(), request.measures()), fields),
                convertRows(result.columnTotals(), concat(request.columns(), request.measures()), fields)
        );
        return new ReportPreviewResponse(responseColumns, rows, Instant.now(), totals);
    }

    private ReportDatasetMetadata metadata(ReportDatasetType type, LanguageEnum language) {
        return switch (type) {
            case SALES -> ReportMetadataFixtures.sales(language);
        };
    }

    private Map<String, FieldMetadata> fields(ReportDatasetMetadata metadata) {
        Map<String, FieldMetadata> fields = new LinkedHashMap<>();
        for (ReportDimensionMetadata dimension : metadata.dimensions()) {
            fields.put(dimension.key(), new FieldMetadata(
                    dimension.name(), dimension.dataType(), dimension.format()
            ));
            dimension.granularities().forEach(granularity -> fields.put(
                    granularity.key(),
                    new FieldMetadata(granularity.name(), ReportDataType.DATE, ReportValueFormat.DATE)
            ));
        }
        for (ReportMeasureMetadata measure : metadata.measures()) {
            fields.put(measure.key(), new FieldMetadata(
                    measure.name(), measure.dataType(), measure.format()
            ));
        }
        return fields;
    }

    private List<String> orderedKeys(ReportPreviewRequest request) {
        return concat(concat(request.rows(), request.columns()), request.measures());
    }

    private List<String> concat(List<String> first, List<String> second) {
        List<String> result = new ArrayList<>(first.size() + second.size());
        result.addAll(first);
        result.addAll(second);
        return result;
    }

    private ReportPreviewColumn column(String key, FieldMetadata field) {
        return new ReportPreviewColumn(key, field.name(), field.dataType(), field.format());
    }

    private List<Map<String, JsonNode>> convertRows(
            List<Map<String, Object>> sourceRows,
            List<String> keys,
            Map<String, FieldMetadata> fields
    ) {
        return sourceRows.stream().map(row -> convertRow(row, keys, fields)).toList();
    }

    private Map<String, JsonNode> convertRow(
            Map<String, Object> source,
            List<String> keys,
            Map<String, FieldMetadata> fields
    ) {
        Map<String, JsonNode> result = new LinkedHashMap<>();
        for (String key : keys) {
            result.put(key, convertValue(source.get(key), requireField(fields, key).dataType()));
        }
        return result;
    }

    private JsonNode convertValue(Object value, ReportDataType type) {
        if (value == null) {
            return NullNode.getInstance();
        }
        return switch (type) {
            case STRING, ENUM -> TextNode.valueOf(value.toString());
            case BOOLEAN -> BooleanNode.valueOf(booleanValue(value));
            case DATE -> TextNode.valueOf(dateValue(value));
            case DECIMAL -> DecimalNode.valueOf(decimalValue(value));
            case INTEGER -> LongNode.valueOf(integerValue(value));
        };
    }

    private boolean booleanValue(Object value) {
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        if (value instanceof String stringValue
                && ("true".equalsIgnoreCase(stringValue) || "false".equalsIgnoreCase(stringValue))) {
            return Boolean.parseBoolean(stringValue);
        }
        throw unexpectedValue(value, ReportDataType.BOOLEAN);
    }

    private String dateValue(Object value) {
        return switch (value) {
            case LocalDate localDate -> localDate.toString();
            case Date sqlDate -> sqlDate.toLocalDate().toString();
            case LocalDateTime localDateTime -> localDateTime.toLocalDate().toString();
            case Timestamp timestamp -> timestamp.toLocalDateTime().toLocalDate().toString();
            case OffsetDateTime offsetDateTime -> offsetDateTime.toLocalDate().toString();
            case Instant instant -> instant.toString();
            case String stringValue -> stringValue;
            default -> throw unexpectedValue(value, ReportDataType.DATE);
        };
    }

    private BigDecimal decimalValue(Object value) {
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        if (value instanceof BigInteger integer) {
            return new BigDecimal(integer);
        }
        if (value instanceof Number number) {
            return new BigDecimal(number.toString());
        }
        if (value instanceof String stringValue) {
            try {
                return new BigDecimal(stringValue);
            } catch (NumberFormatException ignored) {
                throw unexpectedValue(value, ReportDataType.DECIMAL);
            }
        }
        throw unexpectedValue(value, ReportDataType.DECIMAL);
    }

    private long integerValue(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String stringValue) {
            try {
                return Long.parseLong(stringValue);
            } catch (NumberFormatException ignored) {
                throw unexpectedValue(value, ReportDataType.INTEGER);
            }
        }
        throw unexpectedValue(value, ReportDataType.INTEGER);
    }

    private FieldMetadata requireField(Map<String, FieldMetadata> fields, String key) {
        FieldMetadata field = fields.get(key);
        if (field == null) {
            throw new IllegalStateException("Missing response metadata for report field: " + key);
        }
        return field;
    }

    private IllegalStateException unexpectedValue(Object value, ReportDataType type) {
        return new IllegalStateException(
                "Unexpected database value type for " + type + ": " + value.getClass().getName()
        );
    }

    private record FieldMetadata(String name, ReportDataType dataType, ReportValueFormat format) {
    }
}
