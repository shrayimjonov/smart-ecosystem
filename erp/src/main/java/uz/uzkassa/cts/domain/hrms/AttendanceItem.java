package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.Holiday;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.io.Serial;

/**
 * Created date time: 10.02.2025 11:56
 */

@Entity
@Table(name = "attendance_item")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceItem extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 654698657399L;

    @Column(name = "attendance_day", nullable = false)
    Integer attendanceDay;

    @Column(name = "planned_hours")
    Integer plannedHours; // in minutes

    @Column(name = "actual_hours")
    Integer actualHours; // in minutes

    @Column(name = "attendance_id", nullable = false)
    Long attendanceId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", updatable = false, insertable = false)
    Attendance attendance;

    @Column(name = "leave_request_id")
    Long leaveRequestId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_request_id", updatable = false, insertable = false)
    LeaveRequest leaveRequest;

    @Column(name = "holiday_id")
    Long holidayId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_id", updatable = false, insertable = false)
    Holiday holiday;
}
