package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.invoice.request.ResendToXfileRequest;
import uz.uzkassa.cts.dto.sale.invoice.SaleInvoiceResponseDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.service.SaleInvoiceOldService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/sale-invoice")
@Tag(name = "Sale Invoice")
@Slf4j
@RequiredArgsConstructor
public class SaleInvoiceResource {

    private final SaleInvoiceOldService service;

    @Operation(summary ="List")
    @GetMapping("/list")
    public ResponseEntity<Page<SaleInvoiceResponseDto>> getAll(TradeInInvoiceFilter filter) {
        return ResponseEntity.ok(service.list(filter));
    }

    @Operation(summary ="Get")
    @GetMapping("/{id}")
    public ResponseEntity<SaleInvoiceResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }


    @Operation(summary ="Generate Template")
    @GetMapping("/{id}/generate-template")
    public ResponseEntity<String> generateTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(service.getHtmlTemplate(id));
    }

    @Operation(summary = "Delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }


    @Operation(summary ="Get by Sale Contract ID")
    @GetMapping("contract/{saleContractId}")
    public ResponseEntity<SaleInvoiceResponseDto> changeStatus(@PathVariable Long saleContractId) {
        return ResponseEntity.ok(service.getBySaleContractId(saleContractId));
    }

    @Operation(summary ="Resend Invoice to x-file with new date")
    @PostMapping("/resend-with-new-date")
    public ResponseEntity<Void> resendWithNewDate(@Valid @RequestBody ResendToXfileRequest dto) {
        service.resendToXfileWithNewDate(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Download")
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
    public ResponseEntity<SaleInvoiceResponseDto> checkInvoiceXfileStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.checkInvoiceXfileStatus(id));
    }
}
