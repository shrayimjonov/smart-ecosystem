package uz.uzkassa.smartpossupply.report.engine;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record ReportSqlQuery(
        String sql,
        Map<String, Object> parameters
) {

    public ReportSqlQuery {
        if (sql == null || sql.isBlank()) {
            throw new IllegalArgumentException("sql must not be blank");
        }
        parameters = parameters == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(parameters));
    }
}
