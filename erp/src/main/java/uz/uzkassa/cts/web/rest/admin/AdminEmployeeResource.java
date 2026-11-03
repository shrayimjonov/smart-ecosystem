package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.auth.ChangePasswordDTO;
import uz.uzkassa.cts.dto.auth.ChangeUsernameConfirmDTO;
import uz.uzkassa.cts.dto.auth.SecretKeyDTO;
import uz.uzkassa.cts.dto.auth.UsernameDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.employee.admin.EmployeeDetailAdminDTO;
import uz.uzkassa.cts.dto.employee.admin.EmployeeListAdminDTO;
import uz.uzkassa.cts.dto.employee.base.EmployeeDTO;
import uz.uzkassa.cts.dto.file.FileDTO;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.UserStatus;
import uz.uzkassa.cts.filter.EmployeeFilter;
import uz.uzkassa.cts.service.EmployeeService;
import uz.uzkassa.cts.service.ExportReportService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminEmployeeRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminEmployeeResource {

    EmployeeService employeeService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody EmployeeDTO employeeDTO) {
        return ResponseEntity.ok(employeeService.create(employeeDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {
        return ResponseEntity.ok(employeeService.update(id, employeeDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<EmployeeDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeListAdminDTO>> getList(@ParameterObject EmployeeFilter filter) {
        return ResponseEntity.ok(employeeService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject EmployeeFilter filter) {
        return ResponseEntity.ok(employeeService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") UserStatus status) {
        employeeService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/upload/photo")
    public ResponseEntity<FileDTO> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile photo) {
        return ResponseEntity.ok(employeeService.uploadPhoto(id, photo));
    }

    @PostMapping("/{id}/upload/time-track-photo")
    public ResponseEntity<FileDTO> uploadTimeTrackPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile timeTrackPhoto) {
        return ResponseEntity.ok(employeeService.uploadTimeTrackPhoto(id, timeTrackPhoto));
    }

    @DeleteMapping("/{id}/{photoId}/photo")
    public ResponseEntity<UploadFileResponse> removePhoto(@PathVariable(value = "id") Long id, @PathVariable(value = "photoId") Long photoId) {
        employeeService.removePhoto(id, photoId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/time-track-photo/{timeTrackPhotoId}")
    public ResponseEntity<UploadFileResponse> removeTimeTrackPhoto(@PathVariable(value = "id") Long id, @PathVariable(value = "timeTrackPhotoId") Long timeTrackPhotoId) {
        employeeService.removeTimeTrackPhoto(id, timeTrackPhotoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.id + ApiConstants.changePasswordInit)
    public ResponseEntity<SecretKeyDTO> changePasswordInit(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.changePasswordInit(id));
    }

    @PostMapping(ApiConstants.id + ApiConstants.changePasswordConfirm)
    public ResponseEntity<Void> changePasswordConfirm(@PathVariable Long id, @RequestBody ChangePasswordDTO changePasswordDTO) {
        employeeService.changePasswordConfirm(id, changePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.id + ApiConstants.changeUsernameInit)
    public ResponseEntity<SecretKeyDTO> changeUsernameInit(@PathVariable Long id, @RequestBody UsernameDTO usernameDTO) {
        return ResponseEntity.ok(employeeService.changeUsernameInit(id, usernameDTO));
    }

    @PostMapping(ApiConstants.id + ApiConstants.changeUsernameConfirm)
    public ResponseEntity<Void> changeUsernameConfirm(@PathVariable Long id, @RequestBody ChangeUsernameConfirmDTO changeLoginConfirmDTO) {
        employeeService.changeUsernameConfirm(id, changeLoginConfirmDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject EmployeeFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.EMPLOYEE, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject EmployeeFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.EMPLOYEE, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
