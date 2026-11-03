package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.hrms.TimeTrack;
import uz.uzkassa.cts.dto.employee.EmployeeListPhotoDTO;
import uz.uzkassa.cts.dto.report.TimeTrackReportDTO;
import uz.uzkassa.cts.dto.timetrack.CameraTimeTrackDTO;
import uz.uzkassa.cts.dto.timetrack.admin.TimeTrackListAdminDTO;
import uz.uzkassa.cts.dto.timetrack.admin.TimeTrackListByEmployeeAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.TimeTrackReportFilter;

import java.util.List;

public interface TimeTrackService extends BaseService<TimeTrack, Long> {

    Long create(CameraTimeTrackDTO cameraTimeTrackDTO);

    Long createManually(CameraTimeTrackDTO cameraTimeTrackDTO);

    Long update(Long id, CameraTimeTrackDTO cameraTimeTrackDTO);

    Page<TimeTrackListAdminDTO> getListForAdmin(BaseFilter filter);

    Page<TimeTrackListByEmployeeAdminDTO> getListByEmployeeForAdmin(BaseFilter filter);

    List<EmployeeListPhotoDTO> getEmployeeList();

    Page<TimeTrackReportDTO> getReport(TimeTrackReportFilter filter);
}
