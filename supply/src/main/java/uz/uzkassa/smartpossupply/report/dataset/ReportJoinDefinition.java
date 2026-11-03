package uz.uzkassa.smartpossupply.report.dataset;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public record ReportJoinDefinition(
        String key,
        String sql,
        Set<String> dependencies
) {

    private static final Pattern SAFE_KEY = Pattern.compile("[a-z][a-z0-9_]*");

    public ReportJoinDefinition {
        key = requireText(key, "key");
        if (!SAFE_KEY.matcher(key).matches()) {
            throw new IllegalArgumentException("Unsafe join key: " + key);
        }
        sql = requireText(sql, "sql");
        dependencies = dependencies == null ? Set.of() : Set.copyOf(dependencies);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return Objects.requireNonNull(value);
    }
}
