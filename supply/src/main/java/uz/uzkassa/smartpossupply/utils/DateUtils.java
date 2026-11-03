package uz.uzkassa.smartpossupply.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public final class DateUtils {

    public static final String LONG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";
    public static final DateTimeFormatter YEAR_LAST_TWO_DIGIT_FORMAT = DateTimeFormatter.ofPattern("yy");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
    public static final DateTimeFormatter DATE_TIME_HYPHEN_FORMATTER = DateTimeFormatter.ofPattern(LONG_DATE_FORMAT);
    public static final DateTimeFormatter DATE_TIME_HYPHEN_WITHOUT_SECOND_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter DAY_MONTH_YEAR_DOT_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter YEAR_MONTH_DAY_HYPHEN_FORMATTER = DateTimeFormatter.ofPattern(SHORT_DATE_FORMAT);
    public static final DateTimeFormatter YEAR_MONTH_DAY_TIMEZONE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    public static final DateTimeFormatter YEAR_MONTH_DAY_TIMEZONE_MILLIS_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static String formatYearLastTwoDigit(LocalDate date) {
        return YEAR_LAST_TWO_DIGIT_FORMAT.format(date);
    }

    public static LocalDateTime getStartOfMonth() {
        return LocalDateTime.now()
            .with(TemporalAdjusters.firstDayOfMonth())
            .toLocalDate()
            .atStartOfDay();
    }

    public static LocalDateTime getEndOfMonth() {
        return LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth())
            .toLocalDate()
            .atTime(LocalTime.MAX);
    }
}
