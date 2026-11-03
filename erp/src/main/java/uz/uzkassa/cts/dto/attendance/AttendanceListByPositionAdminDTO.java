package uz.uzkassa.cts.dto.attendance;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created date time: 26.02.2025 15:52
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceListByPositionAdminDTO implements Serializable {

    CommonDTO position;

    List<AttendanceByPositionDTO> attendances;
}
