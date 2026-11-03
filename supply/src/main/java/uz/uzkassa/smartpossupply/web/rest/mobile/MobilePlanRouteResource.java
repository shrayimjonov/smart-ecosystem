package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.dto.file.PhotoByteArrayDTO;
import uz.uzkassa.smartpossupply.dto.file.PhotoDTO;
import uz.uzkassa.smartpossupply.dto.planroute.*;
import uz.uzkassa.smartpossupply.enums.DayOfWeekEnum;
import uz.uzkassa.smartpossupply.enums.PlanRouteAttachmentType;
import uz.uzkassa.smartpossupply.enums.PlanRouteCustomerBranchStatus;
import uz.uzkassa.smartpossupply.filters.PlanRouteFilter;
import uz.uzkassa.smartpossupply.service.PlanRouteService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 24.03.2023 16:08
 */
@RestController
@RequestMapping(ApiConstants.mobilePlanRouteRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobilePlanRouteResource {

    PlanRouteService planRouteService;

    @Operation(summary = "Start", operationId = "startPlanRoute")
    @PatchMapping("/start/{planRouteCustomerBranchId}")
    public ResponseEntity<Void> start(@PathVariable Long planRouteCustomerBranchId) {
        planRouteService.start(planRouteCustomerBranchId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Finish", operationId = "finishPlanRoute")
    @PostMapping("/finish")
    public ResponseEntity<Long> finish(@RequestBody PlanRouteFinishDTO planRouteFinishDTO) {
        return ResponseEntity.ok(planRouteService.finish(planRouteFinishDTO));
    }

    @Operation(summary = ApiConstants.Get, operationId = "getPlanRoute")
    @GetMapping("/{id}/{day}")
    public ResponseEntity<PlanRouteDetailDTO> get(@PathVariable Long id, @PathVariable String day) {
        return ResponseEntity.ok(planRouteService.getForMobile(id, DayOfWeekEnum.valueOf(day)));
    }

    @Operation(summary = "Get plan route customer branch by Id", operationId = "getPlanRouteCustomerBranchById")
    @GetMapping("/customer-branches/{planRouteCustomerBranchId}")
    public ResponseEntity<PlanRouteCustomerBranchDetailDTO> getCustomerBranch(@PathVariable Long planRouteCustomerBranchId) {
        return ResponseEntity.ok(planRouteService.getCustomerBranch(planRouteCustomerBranchId));
    }

    @Deprecated
    @Operation(summary = "Check agent location", operationId = "checkLocation")
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkLocation(@Valid @RequestBody PlanRouteCheckLocationDTO checkLocationDTO) {
        return ResponseEntity.ok(planRouteService.checkLocation(checkLocationDTO));
    }

    @Operation(summary = ApiConstants.LookUp, operationId = "planRouteLookup")
    @GetMapping
    public ResponseEntity<List<PlanRouteMobileLookUpDTO>> lookUp(@ParameterObject PlanRouteFilter filter) {
        if (SecurityUtils.getCurrentUserId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(planRouteService.lookUpForMobile(filter));
    }

    @Operation(summary = "Get Current Plan Route", operationId = "getCurrentPlanRoute")
    @GetMapping("/current/{day}")
    public ResponseEntity<PlanRouteDetailDTO> getCurrentPlanRoute(@PathVariable String day, @RequestParam(name = "status", required = false) String status) {
        if (SecurityUtils.getCurrentUserId() == null) {
            return ResponseEntity.ok(new PlanRouteDetailDTO());
        }
        return ResponseEntity.ok(planRouteService.getCurrentPlanRouteForMobile(SecurityUtils.getCurrentUserId(), DayOfWeekEnum.valueOf(day), status != null ? PlanRouteCustomerBranchStatus.valueOf(status) : null));
    }

    @GetMapping("/current/week/offline")
    public ResponseEntity<PlanRouteDetailDTO> getCurrentWeekPlanRoute(@RequestParam(name = "status", required = false) String status) {
        if (SecurityUtils.getCurrentUserId() == null) {
            return ResponseEntity.ok(new PlanRouteDetailDTO());
        }
        return ResponseEntity.ok(planRouteService.getCurrentWeekPlanRouteForMobile(SecurityUtils.getCurrentUserId(), status != null ? PlanRouteCustomerBranchStatus.valueOf(status) : null));
    }

    @Operation(summary = "Upload Photo", operationId = "uploadPhoto")
    @PostMapping(value = "/upload/photo/{planRouteCustomerBranchId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<FileDTO>> uploadPhoto(@PathVariable Long planRouteCustomerBranchId, @RequestPart("files") List<MultipartFile> files, @RequestParam("attachmentType") String attachmentType) {
        return ResponseEntity.ok(planRouteService.uploadPhoto(planRouteCustomerBranchId, files, PlanRouteAttachmentType.valueOf(attachmentType)));
    }

    @Operation(summary = "Upload Photo Using Byte Array", operationId = "uploadPhotoUsingByteArray")
    @PostMapping("/upload/photo/bytes/{planRouteCustomerBranchId}")
    public ResponseEntity<List<FileDTO>> uploadPhotoUsingByteArray(@PathVariable Long planRouteCustomerBranchId, @Valid @RequestBody List<PhotoByteArrayDTO> files, @RequestParam(name = "attachmentType") String attachmentType) {
        return ResponseEntity.ok(planRouteService.uploadPhotoWithBytArray(planRouteCustomerBranchId, files, PlanRouteAttachmentType.valueOf(attachmentType)));
    }

    @Operation(summary = "Upload Photo Using Base64String", operationId = "uploadPhotoUsingBase64String")
    @PostMapping("/upload/photo/base64/{planRouteCustomerBranchId}")
    public ResponseEntity<List<FileDTO>> uploadPhotoUsingBase64String(@PathVariable Long planRouteCustomerBranchId, @Valid @RequestBody List<PhotoDTO> files, @RequestParam("attachmentType") String attachmentType) {
        return ResponseEntity.ok(planRouteService.uploadPhotoWithBase64String(planRouteCustomerBranchId, files, PlanRouteAttachmentType.valueOf(attachmentType)));
    }

    @Deprecated
    @Operation(summary = "Transfer Customer Branch. Deprecated! Use a new API: /api/mobile/v1/plan-route-customer-branches/transfer/{id}?visitPlanDate=?&addToCurrentPlanRoute=?", operationId = "transferCustomerBranch")
    @PatchMapping("/transfer-customer-branch")
    public ResponseEntity<Void> transferCustomerBranch(@RequestParam("planRouteCustomerBranchId") Long planRouteCustomerBranchId,
                                                       @RequestParam("visitPlanDate") LocalDate visitPlanDate) {
        planRouteService.transferCustomerBranch(planRouteCustomerBranchId, visitPlanDate);
        return ResponseEntity.ok().build();
    }

    @Deprecated
    @Operation(summary = "Add unscheduled planRoute for mobile. Deprecated! Use a new API: /api/mobile/v1/plan-route-customer-branches?planRouteItemId&customerBranchId&addToCurrentPlanRoute", operationId = "addUnscheduledPlanRouteCustomerBranch")
    @PostMapping(ApiConstants.id)
    public ResponseEntity<Long> addCustomerBranch(@PathVariable Long id, @RequestBody MobilePlanRouteDTO planRouteDTO) {
        return ResponseEntity.ok(planRouteService.addCustomerBranch(id, planRouteDTO));
    }

    @Deprecated
    @Operation(summary = "Delete unscheduled planRoute for mobile. Deprecated! Use a new API: /api/mobile/v1/plan-route-customer-branches?planRouteId=&customerBranchId", operationId = "deleteUnscheduledPlanRouteCustomerBranch")
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Long> deleteCustomerBranch(@PathVariable Long id, @RequestBody MobilePlanRouteDTO planRouteDTO) {
        return ResponseEntity.ok(planRouteService.deleteCustomerBranch(id, planRouteDTO));
    }

    @Operation(summary = "Get attachments", operationId = "getAttachmentsByPlanRouteCustomerBranchId")
    @GetMapping("/attachments/{planRouteCustomerBranchId}")
    public ResponseEntity<List<PlanRouteCustomerBranchAttachmentDTO>> getAttachments(@PathVariable Long planRouteCustomerBranchId) {
        return ResponseEntity.ok(planRouteService.getAttachments(planRouteCustomerBranchId));
    }

    @Operation(summary = "Delete attachments", operationId = "deleteAttachmentsByPlanRouteCustomerBranchId")
    @DeleteMapping("/attachments/{planRouteCustomerBranchId}")
    public ResponseEntity<Void> deleteAttachments(@PathVariable Long planRouteCustomerBranchId, @RequestBody List<Long> attachmentIds) {
        planRouteService.deleteAttachments(planRouteCustomerBranchId, attachmentIds);
        return ResponseEntity.ok().build();
    }
}
