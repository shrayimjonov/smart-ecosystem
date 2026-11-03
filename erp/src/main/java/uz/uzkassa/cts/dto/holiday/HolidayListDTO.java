package uz.uzkassa.cts.dto.holiday;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CommonStatus;

import java.io.Serial;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HolidayListDTO extends CommonDTO {

    @Serial
    private static final long serialVersionUID = 970387302774L;

    LocalDate holidayDate;

    LocalDate fromDate;

    LocalDate toDate;

    boolean dayOff;

    boolean recurring;

    CommonDTO company;

    CommonStatus status;
}
