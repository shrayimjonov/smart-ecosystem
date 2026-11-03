package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.tradeinact.response.TradeInActResponseDto;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInActPoaRequestDto;
import uz.uzkassa.cts.filter.ActOfTransmissionFilter;
import uz.uzkassa.cts.service.TradeInActService;

@RestController
@RequestMapping("/api/trade-in-act")
@Tag(name = "trade in acts")
@Slf4j
public class TradeInActResource {

    private final TradeInActService service;

    public TradeInActResource(TradeInActService service) {
        this.service = service;
    }


    @Operation(summary ="Get by contract id")
    @GetMapping("/by-contract-id/{id}")
    public ResponseEntity<TradeInActResponseDto> getByContractId(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByContractId(id));
    }

    @Operation(summary ="List")
    @GetMapping("/list")
    public ResponseEntity<Page<TradeInActResponseDto>> list(ActOfTransmissionFilter filter) {
        return ResponseEntity.ok(service.getList(filter));
    }

    @Operation(summary ="Get")
    @GetMapping("/{id}")
    public ResponseEntity<TradeInActResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary ="Generate Template")
    @GetMapping("/{id}/generate-template")
    public ResponseEntity<String> getTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getHtmlTemplate(id));
    }

    @Operation(summary ="Upload File")
    @PostMapping("/{id}/upload")
    public ResponseEntity<TradeInActResponseDto> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload file : {}", file);
        return ResponseEntity.ok(service.uploadFile(id, file));
    }

    @Operation(summary ="Upload Power of Attorney")
    @PostMapping("/{id}/upload-power-attorney")
    public ResponseEntity<TradeInActResponseDto> uploadPowerAttorneyFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload schedule file : {}", file);
        return ResponseEntity.ok(service.uploadPowerAttorneyFile(id, file));
    }

    @Operation(summary ="Accept")
    @PutMapping("/{id}/status/accept")
    public ResponseEntity<TradeInActResponseDto> accept(@PathVariable Long id) {
        return ResponseEntity.ok(service.accept(id));
    }

    @Operation(summary ="Accept")
    @PutMapping("/{id}/status/change-to-attach")
    public ResponseEntity<TradeInActResponseDto> changeToAttach(@PathVariable Long id) {
        return ResponseEntity.ok(service.changeToAttach(id));
    }


    @Operation(summary ="doc flow status")
    @PostMapping("/{id}/change-doc-flow")
    public ResponseEntity<TradeInActResponseDto> changeDocFlowStatus(@Valid @RequestBody ActChangeDocFlowRequestDto dto) {
        return ResponseEntity.ok(service.changeDocFlowStatus(dto));
    }

    @Operation(summary ="Generate POA Template")
    @PostMapping("/generate/poa/template")
    public ResponseEntity<String> generatePoaTemplate(@RequestBody TradeInActPoaRequestDto dto) {
        log.debug("REST request to generate POA template : {}", dto);
        return ResponseEntity.ok(service.generatePoaTemplate(dto));
    }
}
