package uz.uzkassa.horeca.utils;

import java.util.Collection;
import java.util.stream.Collectors;

public final class QueryUtils {

    public static String formatStringListForSql(Collection<String> list) {
        return list.stream()
                .map(s -> "'" + s + "'")
                .collect(Collectors.joining(","));
    }

    public static String formatLongsListForSql(Collection<Long> list) {
        return list.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }
}
