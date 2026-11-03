package uz.uzkassa.cts.dto.holiday;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serial;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HolidayDTO extends CommonDTO {

    @Serial
    private static final long serialVersionUID = 13257368264745L;

    String description;

    LocalDate holidayDate;

    LocalDate fromDate;

    LocalDate toDate;

    boolean dayOff;

    boolean recurring;

    Long companyId;
}
