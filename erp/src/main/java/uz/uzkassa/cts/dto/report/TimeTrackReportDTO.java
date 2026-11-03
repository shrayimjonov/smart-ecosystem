package uz.uzkassa.cts.dto.report;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeTrackReportDTO implements Serializable {

    CommonDTO employee;
    CommonDTO department;
    String timeslotFromTime;
    String timeslotToTime;
    String timeslotTime;
    String lessTime;
    String overTime;
    String actualTime;
    String checkIn;
    String checkOut;

    public TimeTrackReportDTO(Long employeeId, String employeeName, Long departmentId, String departmentName, String timeslotFromTime, String timeslotToTime, String timeslotTime, String lessTime, String overTime, String actualTime, String checkIn, String checkOut) {
        this.employee = new CommonDTO(employeeId, employeeName);
        this.department = departmentId != null ? new CommonDTO(departmentId, departmentName) : null;
        this.timeslotFromTime = timeslotFromTime;
        this.timeslotToTime = timeslotToTime;
        this.timeslotTime = timeslotTime;
        this.lessTime = lessTime;
        this.overTime = overTime;
        this.actualTime = actualTime;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }
}
