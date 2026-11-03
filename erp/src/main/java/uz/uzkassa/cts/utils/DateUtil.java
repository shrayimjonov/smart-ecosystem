package uz.uzkassa.cts.utils;

import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.validator.LocalizationMessage;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

import static uz.uzkassa.cts.constants.MessageConstants.*;

public class DateUtil {

    public static final String LONG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String LONG_DATE_WITHOUT_SECOND_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SHORT_DATE_DOT_FORMAT = "dd.MM.yyyy";
    public static final String TIME_FORMAT = "HH:mm";

    public static final DateTimeFormatter YEAR_LAST_TWO_DIGIT_FORMATTER = DateTimeFormatter.ofPattern("yy");
    public static final DateTimeFormatter LONG_FORMATTER = DateTimeFormatter.ofPattern(LONG_DATE_FORMAT);
    public static final DateTimeFormatter SHORT_FORMATTER = DateTimeFormatter.ofPattern(SHORT_DATE_FORMAT);
    public static final DateTimeFormatter LONG_WITHOUT_SECOND_FORMATTER = DateTimeFormatter.ofPattern(LONG_DATE_WITHOUT_SECOND_FORMAT);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);
    public static final DateTimeFormatter SHORT_DATE_DOT_FORMATTER = DateTimeFormatter.ofPattern(SHORT_DATE_DOT_FORMAT);

    public static String formatYearLastTwoDigit(LocalDate date) {
        return YEAR_LAST_TWO_DIGIT_FORMATTER.format(date);
    }

    public static LocalDate getMonthLastDate() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate getMonthLastDate(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate getMonthFirstDate(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getMonthFirstDate() {
        return getMonthFirstDate(LocalDate.now());
    }

    public static LocalDate calculateDueDate() {
        return getMonthLastDate().plusDays(1);
    }

    public static LocalDate calculateDueDate(LocalDate localDate) {
        return getMonthLastDate(localDate).plusDays(1);
    }

    public static String getLocalDateTimeAsString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return LONG_FORMATTER.format(localDateTime);
    }

    public static String getLocalDateTimeAsStringWithoutSecond(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return LONG_WITHOUT_SECOND_FORMATTER.format(localDateTime);
    }

    /**
     * Get Date start time 2020-01-19T00:00:00
     *
     * @param startDateString 2020-01-19
     * @return LocalDateTime
     */
    public static LocalDateTime getStartDate(String startDateString) {
        if (StringUtils.isEmpty(startDateString)) {
            return null;
        }
        startDateString = startDateString + " 00:00:00";
        LocalDateTime startDate = LocalDateTime.parse(startDateString, LONG_FORMATTER);
        return startDate.with(LocalTime.MIN);
    }

    /**
     * Get Date end time 2020-01-19T23:59:59
     *
     * @param endDateString 2020-01-19
     * @return LocalDateTime
     */
    public static LocalDateTime getEndDate(String endDateString) {
        if (StringUtils.isEmpty(endDateString)) {
            return null;
        }

        endDateString = endDateString + " 23:59:59";
        LocalDateTime endDate = LocalDateTime.parse(endDateString, LONG_FORMATTER);
        return endDate.with(LocalTime.MAX);
    }

    public static LocalDateTime longToLocalDateTime(Long milliseconds) {
        if (milliseconds == null)
            return null;
        return Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static String getLocalDateAsString(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return SHORT_DATE_DOT_FORMATTER.format(localDate);
    }

    public static LocalDate getLastNDayInAMonth(int lastDay) {
        LocalDate date = getMonthLastDate();
        return date.minusDays(lastDay);
    }

    public static long differenceBetweenLocalDate(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public static Duration getDifference(LocalDateTime date1, LocalDateTime date2) {
        return Duration.between(date1, date2);
    }

    public static LocalDateTime getFromStartOfDay(LocalDate from) {
        return from != null ? from.atStartOfDay() : null;
    }

    public static LocalDateTime getToEndOfDay(LocalDate to) {
        return to != null ? to.atTime(LocalTime.MAX) : null;
    }

    public static LocalDateTime convertToLocalTimeZone(LocalDateTime dateTime) {
        ZonedDateTime fromZonedDateTime = dateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime toZonedDateTime = fromZonedDateTime.withZoneSameInstant(ZoneId.of("GMT+5"));
        return toZonedDateTime.toLocalDateTime();
    }

    private static LocalizationMessage getLocalizationMessage() {
        return BeanUtils.getBean(LocalizationMessage.class);
    }

    public static String convertMinutesToString(Integer totalMinutes) {
        LocalizationMessage localizationMessage = getLocalizationMessage();
        if (totalMinutes == null) {
            return localizationMessage.localize(TIME_DEFAULT, new Object[]{String.valueOf(21)});
        }
        int totalHours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        int days = totalHours / 8;
        int hours = totalHours % 8;

        if (days == 0) {
            if (hours == 0) {
                if (minutes != 0) {
                    return localizationMessage.localize(TIME_MINUTE, new Object[]{String.valueOf(minutes)});
                }
            }
            if (hours != 0) {
                if (minutes == 0) {
                    return localizationMessage.localize(TIME_HOURS, new Object[]{String.valueOf(hours)});
                } else {
                    return localizationMessage.localize(TIME_HOUR_MINUTE, new Object[]{String.valueOf(hours), String.valueOf(minutes)});
                }
            }
        }
        if (days != 0) {
            if (hours == 0) {
                if (minutes == 0) {
                    return localizationMessage.localize(TIME_DAY, new Object[]{String.valueOf(days)});
                }
            }
            if (hours != 0) {
                if (minutes == 0) {
                    return localizationMessage.localize(TIME_DAY_HOUR, new Object[]{String.valueOf(days), String.valueOf(hours)});
                } else {
                    return localizationMessage.localize(TIME_DAY_HOUR_MINUTE, String.valueOf(days), String.valueOf(days), String.valueOf(minutes));
                }
            }
        }

        return null;
    }

    public static String convertMinutesToHoursAsString(Integer totalMinutes) {
        LocalizationMessage localizationMessage = getLocalizationMessage();
        if (totalMinutes == null) {
            return "";
        }
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        if (hours == 0) {
            return localizationMessage.localize(TIME_MINUTE, new Object[]{String.valueOf(minutes)});
        }
        return localizationMessage.localize(TIME_HOUR_MINUTE, new Object[]{String.valueOf(hours), String.valueOf(minutes)});
    }
}

