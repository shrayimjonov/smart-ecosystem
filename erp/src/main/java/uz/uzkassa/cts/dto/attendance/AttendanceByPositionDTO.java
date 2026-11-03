package uz.uzkassa.cts.dto.attendance;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

/**
 * Created date time: 26.02.2025 19:00
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceByPositionDTO extends AttendanceBaseDTO {

    CommonDTO department;
}
