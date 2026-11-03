package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceFilter extends BaseFilter {

    int year = LocalDate.now().getYear();

    int month = LocalDate.now().getMonthValue();
}
