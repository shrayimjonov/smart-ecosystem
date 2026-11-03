package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.employee.EmployeeListPhotoDTO;
import uz.uzkassa.cts.dto.report.TimeTrackReportDTO;
import uz.uzkassa.cts.dto.timetrack.CameraTimeTrackDTO;
import uz.uzkassa.cts.dto.timetrack.admin.TimeTrackListAdminDTO;
import uz.uzkassa.cts.dto.timetrack.admin.TimeTrackListByEmployeeAdminDTO;
import uz.uzkassa.cts.enums.TimeTrackType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.TimeTrackReportFilter;
import uz.uzkassa.cts.service.TimeTrackService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminTimeTrackRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminTimeTrackResource {

    TimeTrackService timeTrackService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CameraTimeTrackDTO cameraTimeTrackDTO) {
        return ResponseEntity.ok(timeTrackService.create(cameraTimeTrackDTO));
    }

    @PostMapping("/manual")
    public ResponseEntity<Long> createManually(@RequestBody CameraTimeTrackDTO cameraTimeTrackDTO) {
        return ResponseEntity.ok(timeTrackService.createManually(cameraTimeTrackDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CameraTimeTrackDTO cameraTimeTrackDTO) {
        return ResponseEntity.ok(timeTrackService.update(id, cameraTimeTrackDTO));
    }

    @GetMapping
    public ResponseEntity<Page<TimeTrackListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(timeTrackService.getListForAdmin(filter));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<TimeTrackListByEmployeeAdminDTO>> getListByEmployee(@PathVariable Long employeeId, @ParameterObject BaseFilter filter) {
        filter.setEmployeeId(employeeId);
        return ResponseEntity.ok(timeTrackService.getListByEmployeeForAdmin(filter));
    }

    @GetMapping(ApiConstants.employeeApi)
    public ResponseEntity<List<EmployeeListPhotoDTO>> getEmployeeList() {
        return ResponseEntity.ok(timeTrackService.getEmployeeList());
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<TimeTrackType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(TimeTrackType.class));
    }

    @GetMapping(value = "/report")
    public ResponseEntity<Page<TimeTrackReportDTO>> getReport(@ParameterObject TimeTrackReportFilter filter) {
        return ResponseEntity.ok(timeTrackService.getReport(filter));
    }
}
