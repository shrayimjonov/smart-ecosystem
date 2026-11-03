package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteCustomerBranchInfoDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteUpdateForOfflineDTO;
import uz.uzkassa.smartpossupply.filters.PlanRouteCustomerBranchFilter;
import uz.uzkassa.smartpossupply.service.PlanRouteCustomerBranchService;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping(ApiConstants.mobilePlanRouteCustomerBranchRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobilePlanRouteCustomerBranchResource {

    PlanRouteCustomerBranchService planRouteCustomerBranchService;

    @Operation(summary = "Create Customer branch to plan route", operationId = "addCustomerBranchToPlanRoute")
    @PostMapping
    public ResponseEntity<Void> create(@RequestParam(value = "planRouteItemId") Long planRouteItemId,
                                       @RequestParam(value = "customerBranchId") Long customerBranchId,
                                       @RequestParam(value = "addToCurrentPlanRoute", required = false) Boolean addToCurrentPlanRoute) {
        planRouteCustomerBranchService.create(planRouteItemId, customerBranchId, addToCurrentPlanRoute);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Transfer Customer Branch", description = "id is planRouteCustomerBranchId", operationId = "transferCustomerBranchToPlanRoute")
    @PatchMapping("/transfer/{id}")
    public ResponseEntity<Void> transfer(@PathVariable Long id,
                                         @RequestParam("visitPlanDate") LocalDate visitPlanDate,
                                         @RequestParam(value = "addToCurrentPlanRoute", required = false) Boolean addToCurrentPlanRoute) {
        planRouteCustomerBranchService.transfer(id, visitPlanDate, addToCurrentPlanRoute);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete customer from plan route", operationId = "deleteCustomerBranchFromPlanRoute")
    @DeleteMapping
    public ResponseEntity<Long> delete(@RequestParam(value = "planRouteId") Long planRouteId,
                                       @RequestParam(value = "customerBranchId") Long customerBranchId) {
        planRouteCustomerBranchService.delete(planRouteId, customerBranchId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<PlanRouteCustomerBranchInfoDTO>> getPlanRouteCustomerBranches(@ParameterObject PlanRouteCustomerBranchFilter filter) {
        return ResponseEntity.ok(planRouteCustomerBranchService.getPlanRouteCustomerBranches(filter));
    }

    @PutMapping(ApiConstants.sync)
    public ResponseEntity<Void> syncOffline(@Valid @RequestBody List<PlanRouteUpdateForOfflineDTO> planRouteUpdateForOfflineDTO) {
        planRouteCustomerBranchService.syncOffline(planRouteUpdateForOfflineDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.offline)
    public ResponseEntity<Long> syncOffline(@Valid @RequestBody PlanRouteUpdateForOfflineDTO planRouteUpdateForOfflineDTO) {
        return ResponseEntity.ok(planRouteCustomerBranchService.createOffline(planRouteUpdateForOfflineDTO));
    }
}
