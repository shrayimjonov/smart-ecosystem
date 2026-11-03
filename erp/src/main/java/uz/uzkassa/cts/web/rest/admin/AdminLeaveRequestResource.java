package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.leaverequest.LeaveRequestDTO;
import uz.uzkassa.cts.dto.leaverequest.LeaveRequestDetailDTO;
import uz.uzkassa.cts.dto.leaverequest.LeaveRequestListDTO;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.LeaveRequestService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminLeaveRequestRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminLeaveRequestResource {

    LeaveRequestService leaveRequestService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody LeaveRequestDTO leaveRequestDTO) {
        return ResponseEntity.ok(leaveRequestService.create(leaveRequestDTO));
    }

    @GetMapping
    public ResponseEntity<Page<LeaveRequestListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(leaveRequestService.getList(filter));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<LeaveRequestDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(leaveRequestService.get(id));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        leaveRequestService.deleteById(id, true);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<ApproverCommonStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.of(ApproverCommonStatus.WAITING_FOR_APPROVAL));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.LEAVE_REQUEST, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.LEAVE_REQUEST, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
