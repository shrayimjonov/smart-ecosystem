package uz.uzkassa.cts.dto.holiday;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CommonStatus;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HolidayDetailDTO extends CommonDTO {

    @Serial
    private static final long serialVersionUID = 7475781648637L;

    String description;

    LocalDate holidayDate;

    LocalDate fromDate;

    LocalDate toDate;

    boolean dayOff;

    boolean recurring;

    CommonDTO creator;

    CommonDTO company;

    LocalDateTime createdDate;

    CommonStatus status;
}
