package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.branch.BranchDTO;
import uz.uzkassa.smartpossupply.dto.branch.BranchDetailDTO;
import uz.uzkassa.smartpossupply.dto.branch.BranchListDTO;
import uz.uzkassa.smartpossupply.dto.branch.BranchLookUpDTO;
import uz.uzkassa.smartpossupply.dto.branch.CommonStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.branch.SmartposBranchListDTO;
import uz.uzkassa.smartpossupply.dto.soliq.TaxCompanyBranchDTO;
import uz.uzkassa.smartpossupply.enums.BranchType;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.BranchService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.validator.CommonValidation;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetBranchRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetBranchResource {

    BranchService branchService;

    @PostMapping
    public ResponseEntity<Long> create(@Validated(CommonValidation.class) @RequestBody BranchDTO branchDTO) {
        return ResponseEntity.ok(branchService.create(branchDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<BranchDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.get(id));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @Validated(CommonValidation.class) @RequestBody BranchDTO branchDTO) {
        return ResponseEntity.ok(branchService.update(id, branchDTO));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<CommonStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CommonStatus.class));
    }

    @PatchMapping(ApiConstants.updateType)
    public ResponseEntity<Void> updateType(@PathVariable Long id, @RequestParam String type) {
        branchService.updateType(id, BranchType.valueOf(type));
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<BranchType>> getBranchTypes() {
        return ResponseEntity.ok(EnumSet.allOf(BranchType.class));
    }

    @GetMapping
    public ResponseEntity<Page<BranchListDTO>> getList(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(branchService.getList(filter));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<CommonStatisticsDTO> getStatistics(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new CommonStatisticsDTO());
        }
        return ResponseEntity.ok(branchService.getStatistics(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<BranchLookUpDTO>> lookUp(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return ResponseEntity.ok(branchService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        branchService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-region/{regionId}")
    public ResponseEntity<List<CommonDTO>> getListByRegionId(@PathVariable Long regionId) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(branchService.getListByRegion(regionId));
    }
    
    @GetMapping("/smartpos-branches")
    public ResponseEntity<SmartposBranchListDTO> getSmartposBranches(@RequestParam(required = false) String tin) {
        return ResponseEntity.ok(branchService.getSmartposBranches(tin));
    }

    @GetMapping("/identify-official-info")
    public ResponseEntity<List<TaxCompanyBranchDTO>> getBranchesInfoFromGNK(@RequestParam(required = false) String tin){
        return ResponseEntity.ok(branchService.getBranchesInfoGNK(tin));
    }
}
