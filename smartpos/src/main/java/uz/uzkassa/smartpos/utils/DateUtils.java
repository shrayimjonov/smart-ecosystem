package uz.uzkassa.smartpos.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static final String BASE_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String BASE_DATE_PATTERN_V2 = "dd.MM.yyyy / HH:mm:ss";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(BASE_DATE_PATTERN);
    public static final DateTimeFormatter formatterV2 = DateTimeFormatter.ofPattern(BASE_DATE_PATTERN_V2);

    public static String toString(LocalDateTime dateTime) {
        return formatter.format(dateTime);
    }

    public static String format(LocalDateTime dateTime) {
        return formatterV2.format(dateTime);
    }
}
