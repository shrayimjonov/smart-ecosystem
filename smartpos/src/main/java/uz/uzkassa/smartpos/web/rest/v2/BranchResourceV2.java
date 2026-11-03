package uz.uzkassa.smartpos.web.rest.v2;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.dto.BranchGroupDTO;
import uz.uzkassa.smartpos.dto.BranchSelectItem;
import uz.uzkassa.smartpos.dto.SelectItem;
import uz.uzkassa.smartpos.dto.branch.BranchDTO;
import uz.uzkassa.smartpos.dto.branch.BranchListDTO;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.filter.BranchFilter;
import uz.uzkassa.smartpos.service.BranchService;
import uz.uzkassa.smartpos.service.FileUploadService;
import uz.uzkassa.smartpos.service.LocalizationService;

import java.util.List;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.*;

@RestController
@RequestMapping("/api/v2/branches")
@RequiredArgsConstructor
public class BranchResourceV2 {
    private final BranchService branchService;
    private final FileUploadService fileUploadService;
    private final LocalizationService localizationService;


    @PostMapping("/group")
    @Secured({SMARTPOS_ADMIN, OWNER})
    public ResponseEntity<BranchGroupDTO> createGroup(@Valid @RequestBody BranchGroupDTO dto) {
        BranchGroupDTO result = branchService.createBranchGroup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/group")
    @Secured({SMARTPOS_ADMIN, OWNER})
    public ResponseEntity<BranchGroupDTO> updateGroup(@Valid @RequestBody BranchGroupDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException(localizationService.getMessage("invalid.id"));
        }
        BranchGroupDTO result = branchService.updateBranchGroup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * used only in cabinet
     */
    @GetMapping("/group/select")
    @Secured({SMARTPOS_ADMIN, OWNER, BRANCH_ADMIN, CASHIER})
    public List<SelectItem> selectGroup(@RequestParam(name = "companyId", required = false) Long companyId,
                                        @RequestParam(required = false) String searchKey) {
        return branchService.selectGroup(companyId, searchKey);
    }


    @GetMapping("/group/{groupId}")
    @Secured({SMARTPOS_ADMIN, OWNER, BRANCH_ADMIN, CASHIER})
    public BranchGroupDTO selectGroup(@PathVariable Long groupId) {
        return branchService.findGroupById(groupId);
    }

    @DeleteMapping("/group/{groupId}")
    @Secured({SMARTPOS_ADMIN, OWNER})
    public void deleteGroup(@PathVariable Long groupId) {
        branchService.deleteGroup(groupId);
    }

    @GetMapping
    public Page<BranchListDTO> getAll(@ParameterObject BranchFilter filter) {
        return branchService.findAll(filter);
    }

    @GetMapping("/current")
    public ResponseEntity<BranchDTO> getCurrentBranch() {
        BranchDTO result = branchService.getCurrentBranch();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/selectbox")
    @Secured({SMARTPOS_ADMIN, OWNER, BRANCH_ADMIN, CASHIER})
    public List<BranchSelectItem> search(@ParameterObject BranchFilter filter) {
        return branchService.search(filter);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchDTO> getBranch(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.getBranch(id));
    }

    @PutMapping("/delete")
    @Secured({SMARTPOS_ADMIN, OWNER})
    public ResponseEntity<Void> deleteBranch(@Valid @RequestBody BranchDTO branchDTO) throws EntityNotFound {
        if (branchDTO.getId() == null) {
            throw new BadRequestException(localizationService.getMessage("invalid.id"));
        }
        branchService.deleteBranch(branchDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Secured({SMARTPOS_ADMIN, OWNER})
    public ResponseEntity<Void> deleteBranch(@PathVariable Long id) throws EntityNotFound {
        branchService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/clear-cache")
    @Secured(SMARTPOS_ADMIN)
    public void clearBranchCache() {
        branchService.clearCache(Branch.class.getName());
    }

    @PutMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> uploadLogo(@RequestParam("file") MultipartFile file) {
        FileUpload fileUpload = fileUploadService.fileUpload(file, null, null, true);
        return ResponseEntity.ok(fileUpload.getId());
    }
}
