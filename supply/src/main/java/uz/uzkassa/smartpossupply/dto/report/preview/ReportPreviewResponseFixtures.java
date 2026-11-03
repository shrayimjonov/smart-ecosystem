package uz.uzkassa.smartpossupply.dto.report.preview;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDataType;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportValueFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Temporary deterministic fixtures for the SS-76 OpenAPI contract and local tests.
 */
public final class ReportPreviewResponseFixtures {

    private static final Instant GENERATED_AT = Instant.parse("2026-10-07T05:00:00Z");

    private ReportPreviewResponseFixtures() {
    }

    public static ReportPreviewResponse regionByMonth() {
        return new ReportPreviewResponse(
                columns(),
                List.of(
                        row(text("Ташкент"), text("2026-09"), decimal("12500000.50")),
                        row(JsonNodeFactory.instance.nullNode(), text("2026-09"), decimal("250000.00"))
                ),
                GENERATED_AT,
                new ReportPreviewTotals(
                        Map.of("sales_amount", decimal("12750000.50")),
                        List.of(
                                subtotal("customer_region", text("Ташкент"), "12500000.50"),
                                subtotal("customer_region", JsonNodeFactory.instance.nullNode(), "250000.00")
                        ),
                        List.of(subtotal("document_month", text("2026-09"), "12750000.50"))
                )
        );
    }

    public static ReportPreviewResponse emptyRegionByMonth() {
        return new ReportPreviewResponse(columns(), List.of(), GENERATED_AT);
    }

    public static ReportPreviewResponse topOutlets() {
        return new ReportPreviewResponse(
                topOutletsColumns(),
                List.of(
                        topOutletRow("Торговая точка A", "9500000.25"),
                        topOutletRow("Торговая точка B", "8100000.00"),
                        topOutletRow("Торговая точка C", "6750000.50")
                ),
                GENERATED_AT
        );
    }

    private static List<ReportPreviewColumn> columns() {
        return List.of(
                new ReportPreviewColumn(
                        "customer_region",
                        "Регион торговой точки",
                        ReportDataType.STRING,
                        ReportValueFormat.TEXT
                ),
                new ReportPreviewColumn(
                        "document_month",
                        "Месяц",
                        ReportDataType.DATE,
                        ReportValueFormat.DATE
                ),
                new ReportPreviewColumn(
                        "sales_amount",
                        "Сумма продаж",
                        ReportDataType.DECIMAL,
                        ReportValueFormat.MONEY
                )
        );
    }

    private static List<ReportPreviewColumn> topOutletsColumns() {
        return List.of(
                new ReportPreviewColumn(
                        "customer_outlet",
                        "Торговая точка",
                        ReportDataType.STRING,
                        ReportValueFormat.TEXT
                ),
                new ReportPreviewColumn(
                        "sales_amount",
                        "Сумма продаж",
                        ReportDataType.DECIMAL,
                        ReportValueFormat.MONEY
                )
        );
    }

    private static Map<String, JsonNode> row(JsonNode region, JsonNode month, JsonNode salesAmount) {
        Map<String, JsonNode> row = new LinkedHashMap<>();
        row.put("customer_region", region);
        row.put("document_month", month);
        row.put("sales_amount", salesAmount);
        return row;
    }

    private static Map<String, JsonNode> topOutletRow(String outlet, String salesAmount) {
        Map<String, JsonNode> row = new LinkedHashMap<>();
        row.put("customer_outlet", text(outlet));
        row.put("sales_amount", decimal(salesAmount));
        return row;
    }

    private static Map<String, JsonNode> subtotal(String dimensionKey, JsonNode dimension, String salesAmount) {
        Map<String, JsonNode> row = new LinkedHashMap<>();
        row.put(dimensionKey, dimension);
        row.put("sales_amount", decimal(salesAmount));
        return row;
    }

    private static JsonNode text(String value) {
        return JsonNodeFactory.instance.textNode(value);
    }

    private static JsonNode decimal(String value) {
        return JsonNodeFactory.instance.numberNode(new BigDecimal(value));
    }
}
