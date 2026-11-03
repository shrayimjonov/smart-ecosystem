package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.payment.PaymentContractActDTO;
import uz.uzkassa.cts.dto.payment.PaymentContractDTO;
import uz.uzkassa.cts.dto.payment.PaymentContractDetailResponseDTO;
import uz.uzkassa.cts.filter.PaymentContractFilter;
import uz.uzkassa.cts.service.PaymentContractService;

import java.util.List;

@RestController
@RequestMapping("/api/payment-contract")
@Tag(name = "Payment contract")
@Slf4j
@RequiredArgsConstructor
public class PaymentContractResource {

    private final PaymentContractService paymentContractService;

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<PaymentContractDetailResponseDTO>> list(@ParameterObject PaymentContractFilter filter) {
        return ResponseEntity.ok(paymentContractService.list(filter));
    }

    @Operation(summary = "Create")
    @PostMapping
    public ResponseEntity<PaymentContractDetailResponseDTO> create(@RequestBody @Valid PaymentContractDTO dto) {
        paymentContractService.create(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get")
    @GetMapping(value = "/{id}")
    public ResponseEntity<PaymentContractDetailResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentContractService.getById(id));
    }

    @Operation(summary = "Get by customer")
    @GetMapping(value = "/by-customer/{id}")
    public ResponseEntity<List<PaymentContractDetailResponseDTO>> getByCustomerId(@PathVariable Long id) {
        return ResponseEntity.ok(paymentContractService.getByCustomerId(id));
    }

    @Operation(summary = "Get by customer and status is accepted")
    @GetMapping(value = "/accepted/by-customer/{id}/{deviceType}")
    public ResponseEntity<List<PaymentContractDetailResponseDTO>> getByContractsCustomerIdAndStatus(@PathVariable Long id, @PathVariable String deviceType) {
        return ResponseEntity.ok(paymentContractService.getByContractsIsAccepted(id, deviceType));
    }

    @Operation(summary = "Generate Template")
    @GetMapping(value = "/{id}/generate-template")
    public ResponseEntity<String> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(paymentContractService.generateContractHtml(id));
    }

    @Operation(summary = "Upload file")
    @PostMapping(value = "/{id}/upload-contract")
    public ResponseEntity<PaymentContractDetailResponseDTO> uploadFileContract(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(paymentContractService.uploadContractFile(id, file));
    }

    @Operation(summary = "Add device")
    @PostMapping("/add-device")
    public ResponseEntity<Void> addDevice(@RequestBody @Valid PaymentContractActDTO paymentContractActDTO) {
        paymentContractService.generateAct(paymentContractActDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Accept")
    @PutMapping(value = "/{id}/status/accept")
    public ResponseEntity<PaymentContractDetailResponseDTO> accept(@PathVariable Long id) {
        return ResponseEntity.ok(paymentContractService.accept(id));
    }

    @Operation(summary = "Reject")
    @PutMapping(value = "/{id}/status/reject")
    public ResponseEntity<PaymentContractDetailResponseDTO> reject(@PathVariable Long id) {
        return ResponseEntity.ok(paymentContractService.reject(id));
    }

    @Operation(summary = "Wait for attachment")
    @PutMapping(value = "/{id}/status/waiting-for-attachment")
    public ResponseEntity<PaymentContractDetailResponseDTO> waitingForAttachment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentContractService.waitingForAttach(id));
    }
}
