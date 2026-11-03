package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.group.GroupDTO;
import uz.uzkassa.cts.dto.group.admin.GroupDetailAdminDTO;
import uz.uzkassa.cts.dto.group.admin.GroupListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.GroupService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminGroupRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminGroupResource {

    GroupService groupService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody GroupDTO groupDTO) {
        return ResponseEntity.ok(groupService.create(groupDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody GroupDTO groupDTO) {
        return ResponseEntity.ok(groupService.update(id, groupDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<GroupDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<GroupListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(groupService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(groupService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        groupService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.GROUPS, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.GROUPS, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
