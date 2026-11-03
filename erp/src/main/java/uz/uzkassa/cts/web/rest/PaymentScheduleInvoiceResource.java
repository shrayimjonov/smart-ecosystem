package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.dto.paymentscheduleinvoice.response.PaymentScheduleInvoiceResponseDto;
import uz.uzkassa.cts.filter.PaymentScheduleInvoiceFilter;
import uz.uzkassa.cts.service.PaymentScheduleInvoiceService;

import java.util.List;

@RestController
@RequestMapping("/api/payment-schedule-invoices")
@Tag(name = "payment schedule invoice")
@Slf4j
public class PaymentScheduleInvoiceResource {

    private final PaymentScheduleInvoiceService service;

    public PaymentScheduleInvoiceResource(PaymentScheduleInvoiceService service) {
        this.service = service;
    }


    @Operation(summary ="get by act id")
    @GetMapping("/get-by-act-id/{id}/list")
    public ResponseEntity<List<PaymentScheduleInvoiceResponseDto>> getByContractId(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByActOfTransmissionId(id));
    }

    @Operation(summary ="get by act id")
    @GetMapping("/get-by-schedule-id/{id}")
    public ResponseEntity<PaymentScheduleInvoiceResponseDto> getByScheduleId(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByScheduleId(id));
    }

    @Operation(summary ="get list")
    @GetMapping("/list")
    public ResponseEntity<Page<PaymentScheduleInvoiceResponseDto>> getByContractId(PaymentScheduleInvoiceFilter filter) {
        return ResponseEntity.ok(service.getList(filter));
    }

    @Operation(summary ="generate template")
    @GetMapping("/{id}/generate-template")
    public ResponseEntity<String> getTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTemplateById(id));
    }

    @Operation(summary ="generate template")
    @GetMapping("/get-by-schedule-id/{id}/generate-template")
    public ResponseEntity<String> getTemplateByScheduleId(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTemplateByScheduleId(id));
    }

    @Operation(summary ="get by id")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentScheduleInvoiceResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

}
