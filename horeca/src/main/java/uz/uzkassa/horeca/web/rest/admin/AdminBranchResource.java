package uz.uzkassa.horeca.web.rest.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.branch.BranchDTO;
import uz.uzkassa.horeca.dto.branch.admin.BranchDetailAdminDTO;
import uz.uzkassa.horeca.dto.branch.admin.BranchListAdminDTO;
import uz.uzkassa.horeca.dto.sync.BranchSyncDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.service.BranchService;
import uz.uzkassa.horeca.service.BranchSyncService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(ApiConstants.adminBranchRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class AdminBranchResource {

    BranchService branchService;
    BranchSyncService branchSyncService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody BranchDTO branchDTO) {
        return ResponseEntity.ok(branchService.create(branchDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<BranchDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<BranchListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(branchService.getListForAdmin(filter));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody BranchDTO branchDTO) {
        return ResponseEntity.ok(branchService.update(id, branchDTO));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(required = false) LocalDateTime lastModifiedDate) {
        branchService.delete(id);
        return ResponseEntity.ok().build();
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

    @PostMapping(ApiConstants.sync)
    public ResponseEntity<Long> sync(@RequestBody BranchSyncDTO branchSyncDTO) {
        return ResponseEntity.ok(branchSyncService.sync(branchSyncDTO));
    }
}
