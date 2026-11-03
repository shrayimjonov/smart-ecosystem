package uz.uzkassa.smartpos.web.rest.admin;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.AdminBranchDTO;
import uz.uzkassa.smartpos.dto.BranchSelectItem;
import uz.uzkassa.smartpos.dto.branch.BranchListMapDTO;
import uz.uzkassa.smartpos.dto.customerreceipts.BranchReceiptsDTO;
import uz.uzkassa.smartpos.dto.customerreceipts.BranchReceiptsStatDTO;
import uz.uzkassa.smartpos.dto.filter.CompanyFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.CustomerReceiptsFilter;
import uz.uzkassa.smartpos.filter.BranchFilter;
import uz.uzkassa.smartpos.service.BranchService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminBranchResource {

    private final BranchService branchService;

    @GetMapping("/branches")
    public Page<AdminBranchDTO> getBranches(@ParameterObject CompanyFilter filter) {
        return branchService.getAdminBranches(filter);
    }

    @GetMapping("/branches/map")
    public List<BranchListMapDTO> getBranchesForMap(@ParameterObject CompanyFilter filter) {
        return branchService.getBranchesForMap(filter);
    }

    @GetMapping("/branches/search")
    public ResponseEntity<List<BranchSelectItem>> searchBranch(@ParameterObject BranchFilter filter) {
        filter.setForAdmin(true);
        List<BranchSelectItem> result = branchService.search(filter);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/branch/receipts")
    public Page<BranchReceiptsDTO> getReceiptsByBranch(@ParameterObject CustomerReceiptsFilter filter) {
        return branchService.getReceiptsAmountByBranch(filter);
    }

    @GetMapping("/branch/receipts/stat")
    public BranchReceiptsStatDTO getReceiptsByBranchStat(@ParameterObject CustomerReceiptsFilter filter) {
        return branchService.getReceiptsAmountByBranchStat(filter);
    }

    @DeleteMapping("/branch/{id}")
    public void deleteBranch(@PathVariable Long id) {
        branchService.forceDelete(id);
    }

    @DeleteMapping("/webkassa/{branchId}")
    public void deleteWebkassa(@PathVariable Long branchId) {
        branchService.deleteWebkassa(branchId);
    }
}
