package uz.uzkassa.cts.dto.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

/**
 * Created date time: 11.02.2025 16:42
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendancePayload implements Serializable {

    Long employeeId;

    List<Long> holidayIds;
}
