package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.hrms.Attendance;
import uz.uzkassa.cts.dto.attendance.AttendanceListByDepartmentAdminDTO;
import uz.uzkassa.cts.dto.attendance.AttendanceListByPositionAdminDTO;
import uz.uzkassa.cts.dto.attendance.AttendanceListAdminDTO;
import uz.uzkassa.cts.filter.AttendanceFilter;

import java.util.List;

public interface AttendanceService extends BaseService<Attendance, Long> {

    void create(Long employeeId, List<Long> holidayIds);

    void updateItem(Long employeeId);

    Page<AttendanceListAdminDTO> getListForAdmin(AttendanceFilter filter);

    Page<AttendanceListByDepartmentAdminDTO> getListByDepartmentForAdmin(AttendanceFilter filter);

    Page<AttendanceListByPositionAdminDTO> getListByPositionForAdmin(AttendanceFilter filter);
}
