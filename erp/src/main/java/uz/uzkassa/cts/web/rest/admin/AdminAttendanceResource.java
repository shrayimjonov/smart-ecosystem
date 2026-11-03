package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.attendance.AttendanceListAdminDTO;
import uz.uzkassa.cts.dto.attendance.AttendanceListByDepartmentAdminDTO;
import uz.uzkassa.cts.dto.attendance.AttendanceListByPositionAdminDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.AttendanceFilter;
import uz.uzkassa.cts.service.AttendanceService;
import uz.uzkassa.cts.service.ExportReportService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminAttendanceApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminAttendanceResource {

    AttendanceService attendanceService;
    ExportReportService exportReportService;

    @GetMapping
    public ResponseEntity<Page<AttendanceListAdminDTO>> getList(@ParameterObject AttendanceFilter filter) {
        return ResponseEntity.ok(attendanceService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject AttendanceFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.ATTENDANCE, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject AttendanceFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.ATTENDANCE, ContentType.CSV);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-department")
    public ResponseEntity<Page<AttendanceListByDepartmentAdminDTO>> getListByDepartment(@ParameterObject AttendanceFilter filter) {
        return ResponseEntity.ok(attendanceService.getListByDepartmentForAdmin(filter));
    }

    @GetMapping("/by-position")
    public ResponseEntity<Page<AttendanceListByPositionAdminDTO>> getListByPosition(@ParameterObject AttendanceFilter filter) {
        return ResponseEntity.ok(attendanceService.getListByPositionForAdmin(filter));
    }
}
