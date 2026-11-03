package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.invoice.request.ResendToXfileRequest;
import uz.uzkassa.cts.dto.onetimeorderinvoice.response.OneTimeOrderInvoiceResponseDto;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceResponseDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.OneTimeOrderInvoiceFilter;
import uz.uzkassa.cts.service.OneTimeOrderInvoiceOldService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/one-time-order-invoice")
@Tag(name = "OneTimeOrderInvoice")
@Slf4j
public class OneTimeOrderInvoiceResource {

    private final OneTimeOrderInvoiceOldService service;

    public OneTimeOrderInvoiceResource(OneTimeOrderInvoiceOldService service) {
        this.service = service;
    }

    @Operation(summary ="List of one time order")
    @GetMapping("/list")
    public ResponseEntity<Page<OneTimeOrderInvoiceResponseDto>> getAll(OneTimeOrderInvoiceFilter filter) {
        log.debug("REST request to get all OneTimeOrderFilter : {}", filter);
        return ResponseEntity.ok(service.list(filter));
    }

//    @Operation(summary ="Add New Order")
//    @PostMapping
//    public ResponseEntity<OneTimeOrderResponseDto> create(@Valid @RequestBody OneTimeRequestCreatDto dto) {
//        log.debug("REST request to create new OneTimeRequestDto: {}", dto);
//        return ResponseEntity.ok(service.create(dto));
//    }
//
//    @Operation(summary = "Update order")
//    @PutMapping
//    public ResponseEntity<OneTimeOrderResponseDto> update(@Valid @RequestBody OneTimeRequestDto dto) {
//        log.debug("REST request to update OneTimeRequestDto : {}", dto);
//        return ResponseEntity.ok(service.update(dto));
//    }

    @Operation(summary ="Get order")
    @GetMapping("/{id}")
    public ResponseEntity<OneTimeOrderInvoiceResponseDto> getById(@PathVariable Long id) {
        log.debug("REST request to get OneTimeRequestDto : {}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary ="upload specification")
    @PostMapping("/{id}/upload")
    public ResponseEntity<OneTimeOrderInvoiceResponseDto> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload file : {}", file);
        return ResponseEntity.ok(service.uploadFile(id, file));
    }

    @Operation(summary ="generate specification template")
    @GetMapping("/{id}/generateTemplate")
    public ResponseEntity<String> generateSpecificationTemplate(@PathVariable Long id) {
        log.debug("REST request to get by Id: {}", id);
        String result = service.getSpecificationHtmlTemplate(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "delete order")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to to delete Order by id : {}", id);
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

//    @Operation(summary ="Change Status")
//    @PostMapping("/changeStatus")
//    public ResponseEntity<OneTimeOrderResponseDto> changeStatus(@Valid @RequestBody OneTimeOrderChangeStatusRequestDto dto) {
//        log.debug("REST request to change status: {}", dto);
//        return ResponseEntity.ok(service.changeStatus(dto));
//    }

    @Operation(summary ="Get by contract")
    @GetMapping("contract/{contractId}")
    public ResponseEntity<Page<OneTimeOrderInvoiceResponseDto>> changeStatus(@PathVariable Long contractId, BaseFilter filter) {
        log.debug("REST request get by contractId: {}", contractId);
        return ResponseEntity.ok(service.getByContractId(contractId, filter));
    }

    @Operation(summary ="Get by ID")
    @PostMapping("/{id}/revert")
    public ResponseEntity<ServiceOrderInvoiceResponseDto> revertInvoice(@PathVariable Long id) {
        log.debug("REST request to revert by id: {}", id);
        service.revertInvoice(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="resend invoice to x-file with new date")
    @PostMapping("/resend-with-new-date")
    public ResponseEntity<Void> resendWithNewDate(@Valid  @RequestBody ResendToXfileRequest dto) {
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
    public ResponseEntity<OneTimeOrderInvoiceResponseDto> checkInvoiceXfileStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.checkInvoiceXfileStatus(id));
    }

    @GetMapping("check-invoice-xfile-statuses")
    public ResponseEntity<Void> updateInvoiceXfileStatus() {
        service.updateInvoiceXfileStatuses();
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Recalculate with new Vat")
    @PutMapping("/{id}/recalculate-with-new-vat")
    public ResponseEntity<Void> recalculate(@PathVariable Long id) {
        service.recalculate(id);
        return ResponseEntity.ok().build();
    }

}
