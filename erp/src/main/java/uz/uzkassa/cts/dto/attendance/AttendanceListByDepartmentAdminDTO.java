package uz.uzkassa.cts.dto.attendance;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created date time: 24.02.2025 16:33
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceListByDepartmentAdminDTO implements Serializable {

    CommonDTO department;

    List<AttendanceByDepartmentDTO> attendances;
}
