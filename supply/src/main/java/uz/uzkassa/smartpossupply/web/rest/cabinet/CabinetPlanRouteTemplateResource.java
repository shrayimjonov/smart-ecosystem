package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchLookUpDTO;
import uz.uzkassa.smartpossupply.dto.planroutetemplate.PlanRouteTemplateDTO;
import uz.uzkassa.smartpossupply.dto.planroutetemplate.PlanRouteTemplateDetailDTO;
import uz.uzkassa.smartpossupply.dto.planroutetemplate.PlanRouteTemplateListDTO;
import uz.uzkassa.smartpossupply.dto.planroutetemplate.PlanRouteTemplateLookupDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.DayOfWeekEnum;
import uz.uzkassa.smartpossupply.filters.CustomerBranchFilter;
import uz.uzkassa.smartpossupply.filters.PlanRouteFilter;
import uz.uzkassa.smartpossupply.service.CustomerBranchService;
import uz.uzkassa.smartpossupply.service.PlanRouteTemplateService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping(ApiConstants.cabinetPlanRouteTemplateRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetPlanRouteTemplateResource {

    PlanRouteTemplateService planRouteTemplateService;
    CustomerBranchService customerBranchService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody PlanRouteTemplateDTO planRouteTemplateDTO) {
        return ResponseEntity.ok(planRouteTemplateService.create(planRouteTemplateDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @Valid @RequestBody PlanRouteTemplateDTO planRouteTemplateDTO) {
        return ResponseEntity.ok(planRouteTemplateService.update(id, planRouteTemplateDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<PlanRouteTemplateDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(planRouteTemplateService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<PlanRouteTemplateListDTO>> getList(@ParameterObject PlanRouteFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(planRouteTemplateService.getList(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<PlanRouteTemplateLookupDTO>> lookup(@ParameterObject PlanRouteFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(planRouteTemplateService.lookup(filter));
    }

    @GetMapping("/customer-branches/lookup")
    public ResponseEntity<List<CustomerBranchLookUpDTO>> lookUp(@ParameterObject CustomerBranchFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return ResponseEntity.ok(customerBranchService.lookUpForPlanRoute(filter));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<CommonStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CommonStatus.class));
    }

    @GetMapping(ApiConstants.days)
    public ResponseEntity<EnumSet<DayOfWeekEnum>> getDays() {
        return ResponseEntity.ok(EnumSet.allOf(DayOfWeekEnum.class));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        planRouteTemplateService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        planRouteTemplateService.delete(id);
        return ResponseEntity.ok().build();
    }
}
