package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.payment.PaymentActDetailDTO;
import uz.uzkassa.cts.dto.payment.PaymentActPoaRequestDTO;
import uz.uzkassa.cts.filter.PaymentActFilter;
import uz.uzkassa.cts.service.PaymentActService;

import java.util.List;

@RestController
@RequestMapping("/api/payment-act")
@Tag(name = "Payment Act")
@Slf4j
@RequiredArgsConstructor
public class PaymentActResource {

    private final PaymentActService service;

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<PaymentActDetailDTO>> list(PaymentActFilter filter) {
        return ResponseEntity.ok(service.getList(filter));
    }

    @Operation(summary ="Get by contract ID")
    @GetMapping(value = "/contract-id/{id}")
    public ResponseEntity<List<PaymentActDetailDTO>> getByContractId(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByContractId(id));
    }

    @Operation(summary ="Get")
    @GetMapping(value = "/{id}")
    public ResponseEntity<PaymentActDetailDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary ="Generate Template")
    @GetMapping(value = "/{id}/generate-template")
    public ResponseEntity<String> getTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getHtmlTemplate(id));
    }

    @Operation(summary ="Generate POA Template")
    @PostMapping(value = "/generate/poa/template")
    public ResponseEntity<String> generatePoaTemplate(@RequestBody PaymentActPoaRequestDTO dto) {
        return ResponseEntity.ok(service.generatePoaTemplate(dto));
    }

    @Operation(summary ="Upload File")
    @PostMapping(value = "/{id}/upload")
    public ResponseEntity<PaymentActDetailDTO> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(service.uploadFile(id, file));
    }

    @Operation(summary ="Upload Power of Attorney")
    @PostMapping(value = "/{id}/upload-power-attorney")
    public ResponseEntity<PaymentActDetailDTO> uploadPowerAttorneyFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(service.uploadPowerAttorneyFile(id, file));
    }

    @Operation(summary ="Accept")
    @PutMapping(value = "/{id}/status/accept")
    public ResponseEntity<PaymentActDetailDTO> accept(@PathVariable Long id) {
        return ResponseEntity.ok(service.accept(id));
    }

    @Operation(summary ="Accept to attach")
    @PutMapping(value = "/{id}/status/change-to-attach")
    public ResponseEntity<PaymentActDetailDTO> changeToAttach(@PathVariable Long id) {
        return ResponseEntity.ok(service.changeToAttach(id));
    }

    @Operation(summary ="Defect")
    @PutMapping(value = "/{id}/status/defect")
    public ResponseEntity<PaymentActDetailDTO> defect(@PathVariable Long id) {
        return ResponseEntity.ok(service.defect(id));
    }

    @Operation(summary ="Document Flow Status")
    @PostMapping(value = "/{id}/change-doc-flow")
    public ResponseEntity<PaymentActDetailDTO> changeDocFlowStatus(@Valid @RequestBody ActChangeDocFlowRequestDto dto) {
        return ResponseEntity.ok(service.changeDocFlowStatus(dto));
    }
}
