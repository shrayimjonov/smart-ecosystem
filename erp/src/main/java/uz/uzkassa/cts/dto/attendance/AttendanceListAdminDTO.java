package uz.uzkassa.cts.dto.attendance;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

/**
 * Created date time: 29.01.2025 12:11
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceListAdminDTO extends AttendanceBaseDTO {

    CommonDTO department;

    CommonDTO position;
}
