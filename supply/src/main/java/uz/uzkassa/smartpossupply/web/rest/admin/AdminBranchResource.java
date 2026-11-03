package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.branch.BranchAddressLookupDTO;
import uz.uzkassa.smartpossupply.dto.branch.BranchDTO;
import uz.uzkassa.smartpossupply.dto.branch.BranchDetailDTO;
import uz.uzkassa.smartpossupply.dto.branch.BranchListDTO;
import uz.uzkassa.smartpossupply.dto.branch.BranchLookUpDTO;
import uz.uzkassa.smartpossupply.enums.AppType;
import uz.uzkassa.smartpossupply.enums.BranchType;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.BranchService;
import uz.uzkassa.smartpossupply.validator.AdminValidation;
import uz.uzkassa.smartpossupply.validator.CommonValidation;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminBranchRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminBranchResource {

    BranchService branchService;

    @PostMapping
    public ResponseEntity<Long> create(@Validated(value = {CommonValidation.class, AdminValidation.class}) @RequestBody BranchDTO branchDTO) {
        return ResponseEntity.ok(branchService.create(branchDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<BranchDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<BranchListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(branchService.getList(filter));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @Validated(value = {CommonValidation.class, AdminValidation.class}) @RequestBody BranchDTO branchDTO) {
        return ResponseEntity.ok(branchService.update(id, branchDTO));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<BranchLookUpDTO>> lookUp(@ParameterObject BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return ResponseEntity.ok(branchService.lookUp(filter));
    }

    @GetMapping(ApiConstants.lookUp + "/with-address")
    public ResponseEntity<List<BranchAddressLookupDTO>> branchWithAddressLookup(@ParameterObject BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return ResponseEntity.ok(branchService.lookUpWithAddress(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        branchService.updateStatus(id, CommonStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<CommonStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CommonStatus.class));
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<BranchType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(BranchType.class));
    }

    @GetMapping(ApiConstants.batchSync)
    public ResponseEntity<Void> batchSync(@RequestParam("appType") String appType) {
        branchService.batchSync(AppType.valueOf(appType));
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.sync)
    public ResponseEntity<Void> sync(@RequestParam("id") Long id,
                                     @RequestParam("appType") String appType) {
        branchService.sync(id, AppType.valueOf(appType));
        return ResponseEntity.ok().build();
    }
}
