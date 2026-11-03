package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.actoftransmission.response.ActOfTransmissionFullResponseDto;
import uz.uzkassa.cts.dto.actoftransmission.response.ActOfTransmissionResponseDto;
import uz.uzkassa.cts.dto.actoftransmission.smartpos.ActOfTransmissionSmartposDTO;
import uz.uzkassa.cts.dto.actoftransmission.smartpos.ActPaymentScheduleSmartposDTO;
import uz.uzkassa.cts.dto.contractforleasing.request.GeneratePoaTemplateDTO;
import uz.uzkassa.cts.filter.ActOfTransmissionFilter;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.ActOfTransmissionFactoryService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/act-of-transmission")
@Tag(name = "Order for leasing")
@Slf4j
@AllArgsConstructor
public class ActOfTransmissionResource {

    private final ActOfTransmissionFactoryService service;


    @Operation(summary = "Get by contract id")
    @GetMapping("/contract/{id}/list")
    public ResponseEntity<List<ActOfTransmissionResponseDto>> getByContractId(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByContractId(id));
    }

    @Operation(summary = "List")
    @GetMapping("/list")
    public ResponseEntity<Page<ActOfTransmissionResponseDto>> list(ActOfTransmissionFilter filter) {
        return ResponseEntity.ok(service.getList(filter));
    }

    @Operation(summary = "Prime List")
    @GetMapping("/prime-list")
    public ResponseEntity<Page<ActOfTransmissionSmartposDTO>> primeList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(service.getPrimeList(filter));
    }

    @Operation(summary = "Prime List")
    @GetMapping("/{actId}/schedule-list")
    public ResponseEntity<List<ActPaymentScheduleSmartposDTO>> scheduleList(@PathVariable Long actId) {
        return ResponseEntity.ok(service.getScheduleList(actId));
    }

    @Operation(summary = "Get")
    @GetMapping("/{id}")
    public ResponseEntity<ActOfTransmissionFullResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Generate Template")
    @GetMapping("/{id}/generate-template")
    public ResponseEntity<String> getTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getHtmlTemplate(id));
    }

    @Operation(summary = "Generate Schedule Template")
    @GetMapping("/{id}/generate-schedule-template")
    public ResponseEntity<String> getScheduleTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getScheduleHtmlTemplate(id));
    }

    @Operation(summary = "Upload File")
    @PostMapping("/{id}/upload")
    public ResponseEntity<ActOfTransmissionFullResponseDto> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload file : {}", file);
        return ResponseEntity.ok(service.uploadFile(id, file));
    }

    @Operation(summary = "Upload File Schedule")
    @PostMapping("/{id}/upload-schedule")
    public ResponseEntity<ActOfTransmissionFullResponseDto> uploadScheduleFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload schedule file : {}", file);
        return ResponseEntity.ok(service.uploadScheduleFile(id, file));
    }

    @Operation(summary = "Upload Power of Attorney")
    @PostMapping("/{id}/upload-power-attorney")
    public ResponseEntity<ActOfTransmissionFullResponseDto> uploadPowerAttorneyFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload schedule file : {}", file);
        return ResponseEntity.ok(service.uploadPowerAttorneyFile(id, file));
    }

    @Operation(summary = "Accept")
    @PutMapping("/{id}/status/accept")
    public ResponseEntity<ActOfTransmissionFullResponseDto> accept(@PathVariable Long id, @RequestParam(name = "date") LocalDate date) {
        return ResponseEntity.ok(service.accept(id, date));
    }

    @Operation(summary = "Accept")
    @PutMapping("/{id}/status/change-to-attach")
    public ResponseEntity<ActOfTransmissionFullResponseDto> changeToAttach(@PathVariable Long id) {
        return ResponseEntity.ok(service.changeToAttach(id));
    }

    @Operation(summary = "Defect")
    @PutMapping("/{id}/status/defect")
    public ResponseEntity<ActOfTransmissionFullResponseDto> defect(@PathVariable Long id) {
        return ResponseEntity.ok(service.defect(id));
    }

    @Operation(summary = "Generate POA Template")
    @PostMapping("/generate/poa/template")
    public ResponseEntity<String> generatePoaTemplate(@RequestBody GeneratePoaTemplateDTO dto) {
        log.debug("REST request to generate POA template : {}", dto);
        return ResponseEntity.ok(service.generatePoaTemplate(dto));
    }

    @Operation(summary = "doc flow status")
    @PostMapping("/{id}/change-doc-flow")
    public ResponseEntity<ActOfTransmissionFullResponseDto> changeDocFlowStatus(@Valid @RequestBody ActChangeDocFlowRequestDto dto) {
        return ResponseEntity.ok(service.changeDocFlowStatus(dto));
    }
}
