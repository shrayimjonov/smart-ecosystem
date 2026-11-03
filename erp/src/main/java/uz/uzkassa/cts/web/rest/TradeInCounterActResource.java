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
import uz.uzkassa.cts.dto.tradeincounteract.response.TradeInCounterActResponseDto;
import uz.uzkassa.cts.filter.ActOfTransmissionFilter;
import uz.uzkassa.cts.service.TradeInCounterActService;

@RestController
@RequestMapping("/api/trade-in-counter-act")
@Tag(name = "trade in counter acts")
@Slf4j
public class TradeInCounterActResource {

    private final TradeInCounterActService service;

    public TradeInCounterActResource(TradeInCounterActService service) {
        this.service = service;
    }


    @Operation(summary ="Get by contract id")
    @GetMapping("/by-contract-id/{id}")
    public ResponseEntity<TradeInCounterActResponseDto> getByContractId(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByContractId(id));
    }

    @Operation(summary ="List")
    @GetMapping("/list")
    public ResponseEntity<Page<TradeInCounterActResponseDto>> list(ActOfTransmissionFilter filter) {
        return ResponseEntity.ok(service.getList(filter));
    }

    @Operation(summary ="Get")
    @GetMapping("/{id}")
    public ResponseEntity<TradeInCounterActResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary ="Generate Template")
    @GetMapping("/{id}/generate-template")
    public ResponseEntity<String> getTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getHtmlTemplate(id));
    }

    @Operation(summary ="Upload File")
    @PostMapping("/{id}/upload")
    public ResponseEntity<TradeInCounterActResponseDto> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload file : {}", file);
        return ResponseEntity.ok(service.uploadFile(id, file));
    }

    @Operation(summary ="Upload Power of Attorney")
    @PostMapping("/{id}/upload-power-attorney")
    public ResponseEntity<TradeInCounterActResponseDto> uploadPowerAttorneyFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload schedule file : {}", file);
        return ResponseEntity.ok(service.uploadPowerAttorneyFile(id, file));
    }

    @Operation(summary ="Accept")
    @PutMapping("/{id}/status/accept")
    public ResponseEntity<TradeInCounterActResponseDto> accept(@PathVariable Long id) {
        return ResponseEntity.ok(service.accept(id));
    }

    @Operation(summary ="Accept")
    @PutMapping("/{id}/status/change-to-attach")
    public ResponseEntity<TradeInCounterActResponseDto> changeToAttach(@PathVariable Long id) {
        return ResponseEntity.ok(service.changeToAttach(id));
    }


    @Operation(summary ="doc flow status")
    @PostMapping("/{id}/change-doc-flow")
    public ResponseEntity<TradeInCounterActResponseDto> changeDocFlowStatus(@Valid @RequestBody ActChangeDocFlowRequestDto dto) {
        return ResponseEntity.ok(service.changeDocFlowStatus(dto));
    }
}
