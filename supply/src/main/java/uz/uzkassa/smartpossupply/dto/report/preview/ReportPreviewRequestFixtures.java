package uz.uzkassa.smartpossupply.dto.report.preview;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDatasetType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Temporary deterministic fixture for the SS-75 OpenAPI contract and local tests.
 */
public final class ReportPreviewRequestFixtures {

    private ReportPreviewRequestFixtures() {
    }

    public static ReportPreviewRequest regionByMonth() {
        return new ReportPreviewRequest(
                ReportDatasetType.SALES,
                List.of("customer_region"),
                List.of("document_month"),
                List.of("sales_amount"),
                List.of(
                        new ReportPreviewFilter("document_type", ReportFilterOperator.EQ, text("OUT")),
                        new ReportPreviewFilter(
                                "document_status",
                                ReportFilterOperator.IN,
                                array("COMPLETED", "CHECKING")
                        ),
                        new ReportPreviewFilter(
                                "payment_type",
                                ReportFilterOperator.IN,
                                array("CASH", "TRANSFER")
                        ),
                        new ReportPreviewFilter(
                                "customer_region",
                                ReportFilterOperator.CONTAINS,
                                text("Ташкент")
                        ),
                        new ReportPreviewFilter("marked", ReportFilterOperator.EQ, booleanValue(true))
                ),
                List.of(new ReportPreviewSort("sales_amount", ReportSortDirection.DESC)),
                new ReportPreviewPeriod(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30))
        );
    }

    public static ReportPreviewRequest topOutlets() {
        return new ReportPreviewRequest(
                ReportDatasetType.SALES,
                List.of("customer_outlet"),
                List.of(),
                List.of("sales_amount"),
                List.of(
                        new ReportPreviewFilter(
                                "document_status",
                                ReportFilterOperator.IN,
                                array("COMPLETED")
                        ),
                        new ReportPreviewFilter(
                                "customer_region",
                                ReportFilterOperator.EQ,
                                text("Ташкент")
                        )
                ),
                List.of(new ReportPreviewSort("sales_amount", ReportSortDirection.DESC)),
                new ReportPreviewPeriod(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30))
        );
    }

    private static JsonNode text(String value) {
        return JsonNodeFactory.instance.textNode(value);
    }

    private static JsonNode booleanValue(boolean value) {
        return JsonNodeFactory.instance.booleanNode(value);
    }

    private static JsonNode array(String... values) {
        var array = JsonNodeFactory.instance.arrayNode();
        Arrays.stream(values).forEach(array::add);
        return array;
    }
}
