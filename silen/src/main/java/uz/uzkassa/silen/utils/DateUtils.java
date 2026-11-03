package uz.uzkassa.silen.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public final class DateUtils {
    public final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
    public static final DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public final static DateTimeFormatter ISO_INSTANT = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral('T')
        .append(DateTimeFormatter.ISO_LOCAL_TIME)
        .appendLiteral('Z').toFormatter(Locale.ENGLISH);


    public static String format(final LocalDateTime createdDate) {
        return formatter.format(createdDate);
    }

    public static String format(final LocalDate date) {
        return localDateFormatter.format(date);
    }

    public static LocalDateTime parse(final String date) {
        if(StringUtils.isBlank(date)){
            return null;
        }
        try {
            return LocalDateTime.parse(date, date.contains("-") ? formatter : formatter2);
        } catch (Exception e) {
            return null;
        }
    }
}
