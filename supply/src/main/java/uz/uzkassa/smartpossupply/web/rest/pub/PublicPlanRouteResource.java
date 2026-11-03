package uz.uzkassa.smartpossupply.web.rest.pub;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.enums.PlanRouteAttachmentType;
import uz.uzkassa.smartpossupply.enums.PlanRouteCustomerBranchStatus;
import uz.uzkassa.smartpossupply.enums.PlanRouteReason;
import uz.uzkassa.smartpossupply.enums.PlanRouteStatus;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 28.02.2023 16:05
 */

@RestController
@RequestMapping(ApiConstants.publicPlanRouteRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicPlanRouteResource {

    @Operation(summary = "GET Status", operationId ="getStatus")
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<PlanRouteStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(PlanRouteStatus.class));
    }

    @Operation(summary = "GET Visit Statuses", operationId ="getVisitStatuses")
    @GetMapping("/visit-statuses")
    public ResponseEntity<EnumSet<PlanRouteCustomerBranchStatus>> getVisitStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(PlanRouteCustomerBranchStatus.class));
    }

    @Operation(summary = "GET Reasons", operationId ="getReasons")
    @GetMapping("/reasons")
    public ResponseEntity<EnumSet<PlanRouteReason>> getReason() {
        return ResponseEntity.ok(EnumSet.allOf(PlanRouteReason.class));
    }

    @Operation(summary = "GET attachment types", operationId ="getPlanRouteAttachmentTypes")
    @GetMapping("/attachment-types")
    public ResponseEntity<EnumSet<PlanRouteAttachmentType>> getPlanRouteAttachmentTypes() {
        return ResponseEntity.ok(EnumSet.allOf(PlanRouteAttachmentType.class));
    }
}
