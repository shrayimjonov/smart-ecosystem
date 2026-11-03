package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.BranchSelectItem;
import uz.uzkassa.smartpos.dto.branch.BranchDTO;
import uz.uzkassa.smartpos.dto.branch.BranchListDTO;
import uz.uzkassa.smartpos.dto.branch.CabinetBranchCreateDto;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.filter.BranchFilter;
import uz.uzkassa.smartpos.integration.cto.CtoBranchSyncDTO;
import uz.uzkassa.smartpos.service.BranchService;
import uz.uzkassa.smartpos.service.LocalizationService;

import java.util.List;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.*;


@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchResource {
    private final BranchService branchService;
    private final LocalizationService localizationService;

    @Secured(OWNER)
    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody CabinetBranchCreateDto branchDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(branchService.createNew(branchDTO));
    }

    @Secured(OWNER)
    @PutMapping
    public void update(@Valid @RequestBody CabinetBranchCreateDto branchDTO) {
        if (branchDTO.id() == null) {
            throw new BadRequestException(localizationService.getMessage("invalid.id"));
        }
        branchService.update(branchDTO);
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

    @GetMapping("/selectbox/transfer")
    @Secured({SMARTPOS_ADMIN, OWNER, BRANCH_ADMIN, CASHIER})
    public List<BranchSelectItem> searchForTransfer(@ParameterObject BranchFilter filter) {
        return branchService.searchForTransfer(filter);
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


    @PostMapping("/cts/webhook")
    public void ctsWebhook(@RequestBody @Valid CtoBranchSyncDTO dto) {
        branchService.ctsWebhook(dto);
    }

    @GetMapping("/count-active-branches")
    public Integer countActiveBranches(@RequestParam(required = false) Long id) {
        return branchService.getActiveInstance(id);
    }
}
