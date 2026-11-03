package uz.uzkassa.smartpossupply.report.dataset;

import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDatasetType;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDataType;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.StockStatus;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SalesReportDatasetDefinition {

    public static final String UNAVAILABLE_COST_REASON = "sales.cost.unavailable";
    public static final String MISSING_OUTLET_EXPRESSION = "COALESCE(cb.name, cb.official_name, "
            + "CASE :language WHEN 'uz' THEN 'Ko‘rsatilmagan' WHEN 'cyrillic' THEN 'Кўрсатилмаган' "
            + "ELSE 'Не указана' END)";

    private static final String SIGN = "CASE WHEN s.transfer_type = 'RETURN' THEN -1 ELSE 1 END";

    private SalesReportDatasetDefinition() {
    }

    public static ReportDatasetDefinition create() {
        return new ReportDatasetDefinition(
                ReportDatasetType.SALES,
                "stock_product sp JOIN stock s ON s.id = sp.stock_id",
                "s.company_id = :companyId",
                "s.transfer_date::date",
                List.of(
                        "s.deleted = false",
                        "sp.deleted = false",
                        "s.transfer_type IN ('OUT', 'RETURN')",
                        "s.transfer_date >= :periodFrom",
                        "s.transfer_date < :periodToExclusive"
                ),
                Map.of("document_status", "COMPLETED"),
                joins(),
                dimensions(),
                measures(),
                Set.of(
                        "stock", "stock_product", "product", "category", "customer_branch", "region",
                        "warehouse", "branch", "orders", "users", "company", "plan_route_customer_branch"
                )
        );
    }

    private static Map<String, ReportJoinDefinition> joins() {
        Map<String, ReportJoinDefinition> result = new LinkedHashMap<>();
        result.put("warehouse", join("warehouse", "LEFT JOIN warehouse w ON w.id = s.warehouse_id"));
        result.put("branch", join("branch", "LEFT JOIN branch b ON b.id = s.branch_id"));
        result.put("customer", join("customer", "LEFT JOIN company customer ON customer.id = s.customer_id"));
        result.put("order", join("order", "LEFT JOIN orders o ON o.id = s.order_id"));
        result.put("outlet_route", join(
                "outlet_route",
                "LEFT JOIN plan_route_customer_branch prcb ON prcb.id = o.plan_route_customer_branch_id",
                "order"
        ));
        result.put("outlet", join(
                "outlet",
                "LEFT JOIN customer_branch cb ON cb.id = prcb.customer_branch_id",
                "outlet_route"
        ));
        result.put("outlet_region", join(
                "outlet_region",
                "LEFT JOIN region r ON r.id = cb.region_id",
                "outlet"
        ));
        result.put("agent", join("agent", "LEFT JOIN users agent ON agent.id = o.agent_id", "order"));
        result.put("product", join("product", "LEFT JOIN product p ON p.id = sp.product_id"));
        result.put("category", join("category", "LEFT JOIN category c ON c.id = p.category_id", "product"));
        return result;
    }

    private static Map<String, ReportDimensionDefinition> dimensions() {
        Map<String, ReportDimensionDefinition> result = new LinkedHashMap<>();
        result.put("document_date", dimension("document_date", "s.transfer_date::date", ReportDataType.DATE));
        result.put("document_day", dimension("document_day", "s.transfer_date::date", ReportDataType.DATE));
        result.put("document_week", dimension(
                "document_week", "to_char(s.transfer_date, 'IYYY-\"W\"IW')", ReportDataType.DATE
        ));
        result.put("document_month", dimension(
                "document_month", "to_char(s.transfer_date, 'YYYY-MM')", ReportDataType.DATE
        ));
        result.put("document_quarter", dimension(
                "document_quarter",
                "to_char(s.transfer_date, 'YYYY') || '-Q' || extract(quarter from s.transfer_date)::text",
                ReportDataType.DATE
        ));
        result.put("document_year", dimension(
                "document_year", "to_char(s.transfer_date, 'YYYY')", ReportDataType.DATE
        ));
        result.put("document_type", enumDimension("document_type", "s.transfer_type", Set.of("OUT", "RETURN")));
        result.put("document_status", enumDimension(
                "document_status",
                "s.status",
                enumValues(StockStatus.values())
        ));
        result.put("payment_type", enumDimension(
                "payment_type",
                "s.payment_type",
                enumValues(PaymentType.values())
        ));
        result.put("warehouse", entityDimension("warehouse", "w.name", "w.id", "warehouse"));
        result.put("branch", entityDimension("branch", "b.name", "b.id", "branch"));
        result.put("customer", entityDimension("customer", "customer.name", "customer.id", "customer"));
        result.put("customer_outlet", new ReportDimensionDefinition(
                "customer_outlet",
                MISSING_OUTLET_EXPRESSION,
                List.of("cb.id", MISSING_OUTLET_EXPRESSION),
                Set.of("outlet"),
                true,
                ReportDataType.STRING,
                Set.of()
        ));
        String regionName = "CASE :language WHEN 'uz' THEN r.name_uz WHEN 'cyrillic' THEN r.name_cyrillic ELSE r.name_ru END";
        result.put("customer_region", new ReportDimensionDefinition(
                "customer_region",
                regionName,
                List.of("r.id", regionName),
                Set.of("outlet_region"),
                true,
                ReportDataType.STRING,
                Set.of()
        ));
        String agentName = "trim(concat_ws(' ', agent.first_name, agent.last_name, agent.patronymic))";
        result.put("agent", new ReportDimensionDefinition(
                "agent", agentName, List.of("agent.id", agentName), Set.of("agent"), true,
                ReportDataType.STRING, Set.of()
        ));
        result.put("product", entityDimension("product", "p.name", "p.id", "product"));
        result.put("product_category", entityDimension("product_category", "c.name", "c.id", "category"));
        result.put("brand", new ReportDimensionDefinition(
                "brand", "p.brand", List.of("p.brand"), Set.of("product"), true,
                ReportDataType.STRING, Set.of()
        ));
        result.put("marked", new ReportDimensionDefinition(
                "marked", "p.has_mark", List.of("p.has_mark"), Set.of("product"), true,
                ReportDataType.BOOLEAN, Set.of()
        ));
        return result;
    }

    private static Map<String, ReportMeasureDefinition> measures() {
        Map<String, ReportMeasureDefinition> result = new LinkedHashMap<>();
        result.put("sales_amount", additiveMeasure(
                "sales_amount", "SUM((" + SIGN + ") * COALESCE(sp.total_selling_price, 0))"
        ));
        result.put("sales_amount_without_vat", additiveMeasure(
                "sales_amount_without_vat",
                "SUM((" + SIGN + ") * COALESCE(sp.total_selling_price_without_vat, 0))"
        ));
        result.put("vat_amount", additiveMeasure(
                "vat_amount", "SUM((" + SIGN + ") * COALESCE(sp.total_vat_price, 0))"
        ));
        result.put("quantity", additiveMeasure(
                "quantity", "SUM((" + SIGN + ") * COALESCE(sp.qty, 0))"
        ));
        result.put("discount_amount", additiveMeasure(
                "discount_amount", "SUM((" + SIGN + ") * COALESCE(sp.total_discount_price, 0))"
        ));
        result.put("document_count", nonAdditiveMeasure(
                "document_count", "COUNT(DISTINCT s.id)", Set.of(), ReportDataType.INTEGER
        ));
        result.put("sku_count", nonAdditiveMeasure(
                "sku_count", "COUNT(DISTINCT sp.product_id)", Set.of(), ReportDataType.INTEGER
        ));
        result.put("active_outlet_count", nonAdditiveMeasure(
                "active_outlet_count", "COUNT(DISTINCT cb.id)", Set.of("outlet"), ReportDataType.INTEGER
        ));
        result.put("average_check", nonAdditiveMeasure(
                "average_check",
                "SUM((" + SIGN + ") * COALESCE(sp.total_selling_price, 0)) / NULLIF(COUNT(DISTINCT s.id), 0)",
                Set.of(),
                ReportDataType.DECIMAL
        ));
        result.put("cost_amount", unavailableMeasure("cost_amount"));
        result.put("gross_margin", unavailableMeasure("gross_margin"));
        result.put("gross_margin_percent", unavailableMeasure("gross_margin_percent"));
        return result;
    }

    private static ReportJoinDefinition join(String key, String sql, String... dependencies) {
        return new ReportJoinDefinition(key, sql, Set.of(dependencies));
    }

    private static ReportDimensionDefinition dimension(String key, String expression) {
        return dimension(key, expression, ReportDataType.STRING);
    }

    private static ReportDimensionDefinition dimension(
            String key,
            String expression,
            ReportDataType dataType
    ) {
        return new ReportDimensionDefinition(
                key, expression, List.of(expression), Set.of(), true, dataType, Set.of()
        );
    }

    private static ReportDimensionDefinition enumDimension(
            String key,
            String expression,
            Set<String> values
    ) {
        return new ReportDimensionDefinition(
                key, expression, List.of(expression), Set.of(), true, ReportDataType.ENUM, values
        );
    }

    private static ReportDimensionDefinition entityDimension(
            String key,
            String expression,
            String idExpression,
            String join
    ) {
        return new ReportDimensionDefinition(
                key,
                expression,
                List.of(idExpression, expression),
                Set.of(join),
                true,
                ReportDataType.STRING,
                Set.of()
        );
    }

    private static ReportMeasureDefinition additiveMeasure(String key, String expression) {
        return new ReportMeasureDefinition(
                key, expression, Set.of(), true, true, true, null, ReportDataType.DECIMAL
        );
    }

    private static ReportMeasureDefinition nonAdditiveMeasure(
            String key,
            String expression,
            Set<String> joins,
            ReportDataType dataType
    ) {
        return new ReportMeasureDefinition(key, expression, joins, false, true, true, null, dataType);
    }

    private static ReportMeasureDefinition unavailableMeasure(String key) {
        return new ReportMeasureDefinition(
                key,
                null,
                Set.of(),
                false,
                false,
                false,
                UNAVAILABLE_COST_REASON,
                ReportDataType.DECIMAL
        );
    }

    private static Set<String> enumValues(Enum<?>[] values) {
        return java.util.Arrays.stream(values).map(Enum::name).collect(java.util.stream.Collectors.toUnmodifiableSet());
    }
}
