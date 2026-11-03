package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.approvalprocess.ApprovalProcessDTO;
import uz.uzkassa.cts.dto.approvalprocess.ApprovalProcessDetailAdminDTO;
import uz.uzkassa.cts.enums.ApproveAction;
import uz.uzkassa.cts.enums.ApproveType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.service.ApprovalProcessService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminApprovalProcessRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminApprovalProcessResource {

    ApprovalProcessService approvalProcessService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ApprovalProcessDTO approvalProcessDTO) {
        return ResponseEntity.ok(approvalProcessService.create(approvalProcessDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody ApprovalProcessDTO approvalProcessDTO) {
        return ResponseEntity.ok(approvalProcessService.update(id, approvalProcessDTO));
    }

    @GetMapping("/entity-type/{entityType}/{companyId}")
    public ResponseEntity<ApprovalProcessDetailAdminDTO> getByEntityTypeAndCompany(@PathVariable EntityTypeEnum entityType, @PathVariable Long companyId) {
        return ResponseEntity.ok(approvalProcessService.getByEntityTypeAndCompany(entityType, companyId));
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<ApproveType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(ApproveType.class));
    }

    @GetMapping("/entity-types")
    public ResponseEntity<EnumSet<EntityTypeEnum>> getEntityTypes() {
        return ResponseEntity.ok(EnumSet.of(EntityTypeEnum.LEAVE_REQUEST, EntityTypeEnum.VACANCY_APPLICATION, EntityTypeEnum.VACANCY, EntityTypeEnum.EQUIPMENT_REQUEST, EntityTypeEnum.BENEFIT_REQUEST));
    }

    @GetMapping("/actions")
    public ResponseEntity<EnumSet<ApproveAction>> getActions() {
        return ResponseEntity.ok(EnumSet.allOf(ApproveAction.class));
    }
}
