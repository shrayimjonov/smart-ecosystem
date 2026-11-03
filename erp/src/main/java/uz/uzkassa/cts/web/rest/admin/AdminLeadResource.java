package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.lead.*;
import uz.uzkassa.cts.dto.lead.admin.LeadDetailAdminDTO;
import uz.uzkassa.cts.dto.lead.admin.LeadListAdminDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.LeadFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.LeadService;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@RequestMapping(ApiConstants.adminLeadRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminLeadResource {

    LeadService leadService;
    ExportReportService exportReportService;

    @PostMapping(value = "/hamkor-smartpos")
    public ResponseEntity<Long> createFromHamkorSmartpos(@RequestBody LeadRequestDTO leadRequestDTO) {
        return ResponseEntity.ok(leadService.createFromWebSites(leadRequestDTO, "https://internal.example.com"));
    }

    @PostMapping(value = "/oddiy-smartpos")
    public ResponseEntity<Long> createFromOddiySmartpos(@RequestBody LeadRequestDTO leadRequestDTO) {
        return ResponseEntity.ok(leadService.createFromWebSites(leadRequestDTO, "https://internal.example.com"));
    }

    @PostMapping(value = "/smartpos")
    public ResponseEntity<Long> createFromSmartpos(@RequestBody LeadRequestDTO leadRequestDTO) {
        return ResponseEntity.ok(leadService.createFromWebSites(leadRequestDTO, "https://smartpos.uz"));
    }

    @PostMapping(value = "/shop-smartpos")
    public ResponseEntity<Long> createFromShopSmartpos(@RequestBody LeadRequestDTO leadRequestDTO) {
        return ResponseEntity.ok(leadService.createFromWebSites(leadRequestDTO, "https://internal.example.com"));
    }

    @PostMapping(value = "/nasiya-smartpos")
    public ResponseEntity<Long> createFromNasiyaSmartpos(@RequestBody LeadRequestDTO leadRequestDTO) {
        return ResponseEntity.ok(leadService.createFromWebSites(leadRequestDTO, "https://internal.example.com"));
    }

    @PostMapping(value = "/business-smartpos")
    public ResponseEntity<Long> createFromBusinessSmartpos(@RequestBody LeadRequestDTO leadRequestDTO) {
        return ResponseEntity.ok(leadService.createFromWebSites(leadRequestDTO, "https://internal.example.com"));
    }

    @PostMapping(value = "/smartpos-trade")
    public ResponseEntity<Long> createFromSmartposTrade(@RequestBody LeadSmartposDTO leadSmartposDTO) {
        return ResponseEntity.ok(leadService.createFromSmartpos(leadSmartposDTO, "https://internal.example.com"));
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody LeadDTO leadDTO) {
        return ResponseEntity.ok(leadService.create(leadDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody LeadDTO leadDTO) {
        return ResponseEntity.ok(leadService.update(id, leadDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<LeadDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<LeadListAdminDTO>> getList(@ParameterObject LeadFilter filter) {
        return ResponseEntity.ok(leadService.getList(filter));
    }

    @GetMapping(value = "/smartpos-list/{tin}")
    public ResponseEntity<LeadResponseDTO> getListSmartpos(@PathVariable String tin) {
        return ResponseEntity.ok(leadService.getListSmartpos(tin));
    }

    @PatchMapping(value = "/{id}/state")
    public ResponseEntity<Void> updateState(@PathVariable Long id, @RequestParam("status") LeadState state) {
        leadService.updateState(id, state);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{id}/call-status")
    public ResponseEntity<Void> updateCallStatus(@PathVariable Long id, @RequestParam("status") CallStatus callStatus) {
        leadService.updateCallStatus(id, callStatus);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") LeadStatus leadStatus) {
        leadService.updateStatus(id, leadStatus);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/generate-message/{templateId}/{language}")
    public ResponseEntity<String> generateMessage(@PathVariable Long templateId,
                                                  @PathVariable String language) {
        return ResponseEntity.ok(leadService.generateMessage(templateId, Language.get(language)));
    }

    @PostMapping(value = "/sms")
    public ResponseEntity<Void> sms(@RequestBody LeadSmsDTO leadSmsDTO) {
        leadService.sendSms(leadSmsDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject LeadFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.LEAD, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject LeadFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.LEAD, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
