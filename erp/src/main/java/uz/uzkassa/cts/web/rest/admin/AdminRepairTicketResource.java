package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.repairticket.RepairTicketDTO;
import uz.uzkassa.cts.dto.repairticket.RepairTicketDetailDTO;
import uz.uzkassa.cts.dto.repairticket.RepairTicketItemDTO;
import uz.uzkassa.cts.dto.repairticket.RepairTicketListDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.RepairTicketStatus;
import uz.uzkassa.cts.enums.RepairTicketVisitType;
import uz.uzkassa.cts.enums.RepairType;
import uz.uzkassa.cts.filter.RepairTicketFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.RepairTicketService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminRepairTicketRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminRepairTicketResource {

    RepairTicketService repairTicketService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody RepairTicketDTO repairTicketDTO) {
        return ResponseEntity.ok(repairTicketService.create(repairTicketDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<RepairTicketDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(repairTicketService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<RepairTicketListDTO>> getList(@ParameterObject RepairTicketFilter filter) {
        return ResponseEntity.ok(repairTicketService.getList(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") RepairTicketStatus status) {
        repairTicketService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(ApiConstants.assignMaster)
    public ResponseEntity<Void> assignMaster(@PathVariable Long id, @RequestParam Long masterId) {
        repairTicketService.assignMaster(id, masterId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(ApiConstants.transfer)
    public ResponseEntity<Void> transfer(@PathVariable Long id, @RequestParam Long branchId) {
        repairTicketService.transfer(id, branchId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(ApiConstants.accept)
    public ResponseEntity<Void> accept(@PathVariable Long id) {
        repairTicketService.accept(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(ApiConstants.updateType)
    public ResponseEntity<Void> updateType(@PathVariable Long id, @RequestParam Long typeId) {
        repairTicketService.updateType(id, typeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(ApiConstants.updateRepairType)
    public ResponseEntity<Void> updateRepairType(@PathVariable Long id, @RequestParam RepairType repairType) {
        repairTicketService.updateRepairType(id, repairType);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(ApiConstants.equipment)
    public ResponseEntity<Void> updateEquipments(@PathVariable Long id, @RequestParam List<Long> equipmentIds) {
        repairTicketService.updateEquipments(id, equipmentIds);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/items")
    public ResponseEntity<Void> updateItems(@PathVariable Long id, @RequestBody List<RepairTicketItemDTO> items) {
        repairTicketService.updateItems(id, items);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<RepairTicketStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(RepairTicketStatus.class));
    }

    @GetMapping(ApiConstants.visitTypes)
    public ResponseEntity<EnumSet<RepairTicketVisitType>> getVisitTypes() {
        return ResponseEntity.ok(EnumSet.allOf(RepairTicketVisitType.class));
    }

    @GetMapping(ApiConstants.repairTypes)
    public ResponseEntity<EnumSet<RepairType>> getRepairTypes() {
        return ResponseEntity.ok(EnumSet.allOf(RepairType.class));
    }

    @PatchMapping("/{id}/update-repair-status")
    public ResponseEntity<Void> updateRepairStatus(@PathVariable Long id, Long repairStatusId) {
        repairTicketService.updateRepairStatus(id, repairStatusId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/generate-accept-act-template")
    public ResponseEntity<String> generateAcceptActHtml(@PathVariable Long id) {
        return ResponseEntity.ok(repairTicketService.getRepairTicketAcceptActHtmlTemplate(id));
    }

    @GetMapping("/{id}/generate-defect-act-template")
    public ResponseEntity<String> generateDefectActHtml(@PathVariable Long id) {
        return ResponseEntity.ok(repairTicketService.getRepairTicketDefectActHtmlTemplate(id));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject RepairTicketFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.REPAIR_TICKET, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject RepairTicketFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.REPAIR_TICKET, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
