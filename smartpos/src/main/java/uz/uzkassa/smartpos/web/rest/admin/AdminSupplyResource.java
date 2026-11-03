package uz.uzkassa.smartpos.web.rest.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.CompanyCommonDTO;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.service.BranchService;
import uz.uzkassa.smartpos.service.SupplyReceiptService;

@RequestMapping("/api/v1/admin/supply")
@RestController
@RequiredArgsConstructor
public class AdminSupplyResource {
    private final BranchService branchService;
    private final SupplyReceiptService supplyReceiptService;

    @GetMapping("/company/{tin}/branches")
    public CompanyCommonDTO getBranches(@PathVariable String tin) {
        return branchService.getBranchesUsingCompanyTin(tin);
    }

    @GetMapping(value = "/receipt/{uid}")
    public ResponseEntity<ReceiptDTO> getByUid(@PathVariable("uid") String uid) {
        return ResponseEntity.ok(supplyReceiptService.getByUid(uid));
    }

    @PostMapping("/receipt")
    public void saveSupplyReceipt(@RequestBody @Valid ReceiptDTO receiptDTO) {
        supplyReceiptService.saveSupplyReceipt(receiptDTO);
    }
}
