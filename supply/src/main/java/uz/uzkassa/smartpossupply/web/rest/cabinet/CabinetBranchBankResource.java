package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.bankbranch.BranchBankDTO;
import uz.uzkassa.smartpossupply.dto.bankbranch.BranchBankDetailDTO;
import uz.uzkassa.smartpossupply.service.BranchBankService;

@RestController
@RequestMapping(ApiConstants.cabinetBranchBankRootApi)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CabinetBranchBankResource {

    BranchBankService branchBankService;

    @GetMapping("/{branchId}")
    public ResponseEntity<BranchBankDetailDTO> getByBranchId(@PathVariable Long branchId) {
        return ResponseEntity.ok(branchBankService.getByBranchId(branchId));
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Valid BranchBankDTO branchBankDTO) {
        return ResponseEntity.ok(branchBankService.createOrUpdate(branchBankDTO));
    }
}
