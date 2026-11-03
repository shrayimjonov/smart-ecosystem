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
import uz.uzkassa.cts.dto.paymentCompanyContract.PaymentCompanyContractDTO;
import uz.uzkassa.cts.dto.paymentCompanyContract.PaymentCompanyContractDetailDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.PaymentCompanyContractService;

import java.util.List;

@RestController
@RequestMapping("/api/payment-company-contract")
@Tag(name = "Payment company contract")
@Slf4j
@RequiredArgsConstructor
public class PaymentCompanyContractResource {

    private final PaymentCompanyContractService paymentCompanyContractService;

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<PaymentCompanyContractDetailDTO>> list(BaseFilter filter) {
        return ResponseEntity.ok(paymentCompanyContractService.list(filter));
    }

    @Operation(summary ="Create")
    @PostMapping
    public ResponseEntity<PaymentCompanyContractDetailDTO> create(@RequestBody @Valid PaymentCompanyContractDTO dto) {
        return ResponseEntity.ok(paymentCompanyContractService.create(dto));
    }

    @Operation(summary ="Get")
    @GetMapping(value = "/{id}")
    public ResponseEntity<PaymentCompanyContractDetailDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentCompanyContractService.getById(id));
    }

    @Operation(summary ="Get by company")
    @GetMapping(value = "/by-company/{id}")
    public ResponseEntity<List<PaymentCompanyContractDetailDTO>> getByCustomerId(@PathVariable Long id) {
        return ResponseEntity.ok(paymentCompanyContractService.getByCompanyId(id));
    }

    @Operation(summary ="Get by company and status is accepted")
    @GetMapping(value = "/accepted/by-company/{id}")
    public ResponseEntity<List<PaymentCompanyContractDetailDTO>> getByContractsCompanyIdAndStatus(@PathVariable Long id) {
        return ResponseEntity.ok(paymentCompanyContractService.getContractsByStatusIsAccepted(id));
    }

    @Operation(summary ="Generate Template")
    @GetMapping(value = "/{id}/generate-template")
    public ResponseEntity<String> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(paymentCompanyContractService.generateContractHtml(id));
    }

    @Operation(summary ="Upload file")
    @PostMapping(value = "/{id}/upload-contract")
    public ResponseEntity<PaymentCompanyContractDetailDTO> uploadFileContract(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(paymentCompanyContractService.uploadContractFile(id, file));
    }
}
