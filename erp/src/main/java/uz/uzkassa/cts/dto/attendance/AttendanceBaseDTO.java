package uz.uzkassa.cts.dto.attendance;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created date time: 26.02.2025 18:32
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceBaseDTO implements Serializable {

    CommonDTO employee;

    Integer monthlyPlannedHours;

    Integer monthlyActualHours;

    Integer normalDays;

    Integer actualDays;

    List<AttendanceItemDTO> items = new ArrayList<>();

    public void addItem(AttendanceItemDTO item) {
        this.items.add(item);
    }
}
