package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.invoice.request.ResendToXfileRequest;
import uz.uzkassa.cts.dto.tradeininvoice.response.TradeInInvoiceResponseDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.service.TradeInInvoiceOldService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/trade-in-invoice")
@Tag(name = "trade in invoice")
@Slf4j
public class TradeInInvoiceResource {

    private final TradeInInvoiceOldService service;

    public TradeInInvoiceResource(TradeInInvoiceOldService service) {
        this.service = service;
    }

    @Operation(summary = "List of one time order")
    @GetMapping("/list")
    public ResponseEntity<Page<TradeInInvoiceResponseDto>> getAll(@ParameterObject TradeInInvoiceFilter filter) {
        log.debug("REST request to get all OneTimeOrderFilter : {}", filter);
        return ResponseEntity.ok(service.list(filter));
    }

    @Operation(summary = "Get order")
    @GetMapping("/{id}")
    public ResponseEntity<TradeInInvoiceResponseDto> getById(@PathVariable Long id) {
        log.debug("REST request to get OneTimeRequestDto : {}", id);
        return ResponseEntity.ok(service.getById(id));
    }


    @Operation(summary = "generate specification template")
    @GetMapping("/{id}/generateTemplate")
    public ResponseEntity<String> generateTemplate(@PathVariable Long id) {
        log.debug("REST request to get by Id: {}", id);
        String result = service.getHtmlTemplate(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "delete order")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to to delete Order by id : {}", id);
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Get by contract")
    @GetMapping("contract/{contractId}")
    public ResponseEntity<TradeInInvoiceResponseDto> changeStatus(@PathVariable Long contractId) {
        log.debug("REST request get by contractId: {}", contractId);
        return ResponseEntity.ok(service.getByContractId(contractId));
    }

    @Operation(summary = "Get by ID")
    @PostMapping("/{id}/revert")
    public ResponseEntity<Void> revertInvoice(@PathVariable Long id) {
        log.debug("REST request to revert by id: {}", id);
        service.revertInvoice(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "resend invoice to x-file with new date")
    @PostMapping("/resend-with-new-date")
    public ResponseEntity<Void> resendWithNewDate(@Valid @RequestBody ResendToXfileRequest dto) {
        log.debug("REST request to get by id: {}", dto);
        service.resendToXfileWithNewDate(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "download Url")
    @GetMapping("download/{from}/{to}/{status}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable LocalDate from,
                                                  @PathVariable LocalDate to,
                                                  @PathVariable ContractStatus status) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"act-details.xlsx\"")
                .body(service.download(from, to, status));
    }

    @GetMapping("/{id}/check-invoice-xfile-status")
    public ResponseEntity<TradeInInvoiceResponseDto> checkInvoiceXfileStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.checkInvoiceXfileStatus(id));
    }

    @GetMapping("/check-invoice-xfile-statuses")
    public ResponseEntity<Void> updateInvoiceXfileStatuses() {
        service.updateInvoiceXfileStatuses();
        return ResponseEntity.ok().build();
    }
}
