package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.io.Serial;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created date time: 28.01.2025 10:53
 */

@Entity
@Table(name = "attendance")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Attendance extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 654698657398L;

    @Column(name = "attendance_date", nullable = false)
    LocalDate attendanceDate;

    @Column(name = "monthly_planned_hours")
    Integer monthlyPlannedHours; // in minutes

    @Column(name = "monthly_actual_hours")
    Integer monthlyActualHours; // in minutes

    @Column(name = "normal_days")
    Integer normalDays;

    @Column(name = "actual_days")
    Integer actualDays;

    @Column(name = "employee_id")
    Long employeeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", updatable = false, insertable = false)
    User employee;

    @OneToMany(mappedBy = "attendance", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("attendanceDay")
    List<AttendanceItem> items = new ArrayList<>();

    public void addItem(AttendanceItem attendanceItem) {
        attendanceItem.setAttendance(this);
        attendanceItem.setAttendanceId(this.id);
        items.add(attendanceItem);
    }
}
