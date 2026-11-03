package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.actoftransmissioninvoice.response.ActOfTransmissionInvoiceListResponseDto;
import uz.uzkassa.cts.dto.actoftransmissioninvoice.response.ActOfTransmissionInvoiceResponseDto;
import uz.uzkassa.cts.dto.invoice.request.ResendToXfileRequest;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.PaymentScheduleInvoiceFilter;
import uz.uzkassa.cts.service.ActOfTransmissionInvoiceOldService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/act-of-transmission-invoices")
@Tag(name = "act of transmission invoice")
@Slf4j
public class ActOfTransmissionInvoiceResource {

    private final ActOfTransmissionInvoiceOldService service;

    public ActOfTransmissionInvoiceResource(ActOfTransmissionInvoiceOldService service) {
        this.service = service;
    }

    @Operation(summary ="get by act id")
    @GetMapping("/get-by-act-id/{id}")
    public ResponseEntity<ActOfTransmissionInvoiceResponseDto> getByContractId(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByActOfTransmissionId(id));
    }

    @Operation(summary ="get list")
    @GetMapping("/list")
    public ResponseEntity<Page<ActOfTransmissionInvoiceListResponseDto>> getByContractId(PaymentScheduleInvoiceFilter filter) {
        return ResponseEntity.ok(service.getList(filter));
    }

    @Operation(summary ="generate template")
    @GetMapping("/{id}/generate-template")
    public ResponseEntity<String> getTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTemplateById(id));
    }

    @Operation(summary ="generate template")
    @GetMapping("/get-by-act-id/{id}/generate-template")
    public ResponseEntity<String> getTemplateByActId(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTemplateById(id));
    }

    @Operation(summary ="get by id")
    @GetMapping("/{id}")
    public ResponseEntity<ActOfTransmissionInvoiceResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary ="resend invoice to x-file with new date")
    @PostMapping("/resend-with-new-date")
    public ResponseEntity<Void> resendWithNewDate(@Valid ResendToXfileRequest dto) {
        log.debug("REST request to get by id: {}", dto);
        service.resendInvoiceToXfileWithNewDate(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="download Url")
    @GetMapping("download/{from}/{to}/{status}")
    public ResponseEntity<String> downloadInvoice(@PathVariable LocalDate from,
                                                  @PathVariable LocalDate to,
                                                  @PathVariable ContractStatus status) {
        log.debug("REST request to get by from: {}", from);
        log.debug("REST request to get by to: {}", to);
        log.debug("REST request to get by Status: {}", status);
        String result = service.getInvoicesDownloadUrl(from, to, status);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/check-invoice-xfile-status")
    public ResponseEntity<ActOfTransmissionInvoiceResponseDto> checkInvoiceXfileStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.checkInvoiceXfileStatus(id));
    }


    @GetMapping("/update-invoice-xfile-status")
    public ResponseEntity<Void> updateInvoiceXfileStatus() {
        service.updateInvoiceXfileStatus();
        return ResponseEntity.ok().build();
    }
}
