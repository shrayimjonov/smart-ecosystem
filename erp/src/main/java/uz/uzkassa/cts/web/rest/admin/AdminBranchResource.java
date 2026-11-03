package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.CommonTreeDTO;
import uz.uzkassa.cts.dto.branch.BranchDTO;
import uz.uzkassa.cts.dto.branch.admin.BranchDetailAdminDTO;
import uz.uzkassa.cts.dto.branch.admin.BranchListAdminDTO;
import uz.uzkassa.cts.dto.branch.admin.BranchStatisticsAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.BranchService;
import uz.uzkassa.cts.service.ExportReportService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminBranchRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminBranchResource {

    BranchService branchService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody BranchDTO branchDTO) {
        return ResponseEntity.ok(branchService.create(branchDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody BranchDTO branchDTO) {
        return ResponseEntity.ok(branchService.update(id, branchDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<BranchDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<BranchListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(branchService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.tree)
    public ResponseEntity<List<CommonTreeDTO>> treeList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(branchService.treeList(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(branchService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        branchService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.BRANCH, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.BRANCH, ContentType.CSV);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        branchService.deleteById(id, true);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.idStatistics)
    public ResponseEntity<BranchStatisticsAdminDTO> getStatistics(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.getStatistics(id));
    }
}
