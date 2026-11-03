package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.invoice.request.ChangeInvoiceOwnerRequestDto;
import uz.uzkassa.cts.dto.invoice.request.InvoiceCalculateDto;
import uz.uzkassa.cts.dto.invoice.request.ResendToXfileRequest;
import uz.uzkassa.cts.dto.invoice.response.InvoiceResponseDto;
import uz.uzkassa.cts.dto.serviceorderinvoice.request.RevertInvoiceRequestDto;
import uz.uzkassa.cts.dto.serviceorderinvoice.request.ServiceInvoiceRequestDto;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceListDto;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceResponseDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.ServiceOrderInvoiceFilter;
import uz.uzkassa.cts.service.ServiceOrderInvoiceOldService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/serviceOrderInvoice")
@Tag(name = "Service Order Invoice")
@Slf4j
public class ServiceOrderInvoiceResource {

    private final ServiceOrderInvoiceOldService service;

    public ServiceOrderInvoiceResource(ServiceOrderInvoiceOldService service) {
        this.service = service;
    }


    @Operation(summary ="Add new invoice")
    @GetMapping("/specification/{specificationId}")
    public ResponseEntity<InvoiceCalculateDto> calculate(@PathVariable Long specificationId) {
        log.debug("REST request to calculate balance: {}", specificationId);
        InvoiceCalculateDto result = service.calculate(specificationId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="Add new invoice")
    @PostMapping
    public ResponseEntity<ServiceOrderInvoiceResponseDto> create(@Valid @RequestBody ServiceInvoiceRequestDto dto) {
        log.debug("REST request to create new OrderDto: {}", dto);
        ServiceOrderInvoiceResponseDto result = service.create(dto);
        return ResponseEntity.ok(result);
    }


    @Operation(summary ="Get by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrderInvoiceResponseDto> get(@PathVariable Long id) {
        log.debug("REST request to get by id: {}", id);
        ServiceOrderInvoiceResponseDto result = service.getById(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="Recalculate with new Vat")
    @PutMapping("/{id}/recalculate-with-new-vat")
    public ResponseEntity<Void> recalculate(@PathVariable Long id) {
        service.recalculate(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Get by ID")
    @PostMapping("/{id}/revert")
    public ResponseEntity<ServiceOrderInvoiceResponseDto> revertInvoice(@PathVariable Long id) {
        log.debug("REST request to revert by id: {}", id);
        service.revertInvoice(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Get by ID")
    @PostMapping("/revert")
    public ResponseEntity<ServiceOrderInvoiceResponseDto> revertInvoice(@RequestBody RevertInvoiceRequestDto revertDto) {
        log.debug("REST request to revert by id: {}", revertDto);
        service.revertInvoiceList(revertDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Get by ID")
    @GetMapping("/contract/{contractId}")
    public ResponseEntity<Page<ServiceOrderInvoiceResponseDto>> getByContractId(@PathVariable Long contractId, BaseFilter filter) {
        log.debug("REST request to get by id: {}", contractId);
        Page<ServiceOrderInvoiceResponseDto> result = service.getByContractId(contractId, filter);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="upload invoice")
    @PostMapping("/{id}/upload")
    public ResponseEntity<ServiceOrderInvoiceResponseDto> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload file : {}", file);
        ServiceOrderInvoiceResponseDto result = service.uploadFile(id, file);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="generate power attorney template")
    @GetMapping("/{id}/generateTemplate")
    public ResponseEntity<String> generateTemplate(@PathVariable Long id) {
        log.debug("REST request to get by Id: {}", id);
        String result = service.getInvoiceTemplate(id);
        return ResponseEntity.ok(result);
    }


    @Operation(summary ="invoice list")
    @GetMapping("/{from}/{to}")
    public ResponseEntity<List<ServiceOrderInvoiceListDto>> generateTemplates(@PathVariable LocalDate from, @PathVariable LocalDate to) {
        log.debug("REST request to get by from: {}", from);
        log.debug("REST request to get by to: {}", to);
        List<ServiceOrderInvoiceListDto> result = service.getInvoices(from, to, ContractStatus.ACCEPTED);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="download Url")
    @GetMapping("download/{from}/{to}/{status}")
    public ResponseEntity<String> downloadInvoice(@PathVariable LocalDate from,
                                                  @PathVariable LocalDate to,
                                                  @PathVariable ContractStatus status) {
        log.debug("REST request to get by from: {}", from);
        log.debug("REST request to get by to: {}", to);
        log.debug("REST request to get by status: {}", status);
        String result = service.getInvoicesDownloadUrl(from, to, status);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="list")
    @GetMapping("/list")
    public ResponseEntity<Page<ServiceOrderInvoiceListDto>> listInvoice(ServiceOrderInvoiceFilter filter) {
        log.debug("REST request Page<ServiceOrderInvoiceListDto>: {}", filter);
        Page<ServiceOrderInvoiceListDto> result = service.getInvoicesList(filter);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="get act list")
    @GetMapping("/get-details-count")
    public ResponseEntity<Long> getActsCount(ServiceOrderInvoiceFilter filter) {
        log.debug("REST request get acts count: {}", filter);
        Long result = service.getDetailsCount(filter);
        return ResponseEntity.ok(result);
    }


    @Operation(summary ="delete invoice")
    @DeleteMapping("/{id}")
    public ResponseEntity<InvoiceResponseDto> delete(@PathVariable Long id) {
        log.debug("REST request to get by id: {}", id);
        service.deleteByIdIfNotAccepted(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="resend invoice to x-file with new date")
    @PostMapping("/resend-with-new-date")
    public ResponseEntity<Void> resendWithNewDate(@Valid @RequestBody ResendToXfileRequest dto) {
        log.debug("REST request to get by id: {}", dto);
        service.resendInvoiceToXfileWithNewDate(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="fix")
    @GetMapping("/fix")
    public ResponseEntity<Void> fix() {
        log.debug("REST request to fix invoice");
        service.fixInvoices();
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="change invoice owner and change balance")
    @PostMapping("/change-invoice-owner/change-balance")
    public ResponseEntity<Void> changeInvoiceOwnerChangeBalance(@Valid @RequestBody ChangeInvoiceOwnerRequestDto dto) {
        log.debug("REST request to get by id: {}", dto);
        ServiceOrderInvoiceResponseDto responseDto = service.changeInvoiceOwnerAndChangeBalance(dto);
        ResendToXfileRequest request = new ResendToXfileRequest();
        request.setId(responseDto.getId());
        request.setDate(responseDto.getInvoiceDate().toLocalDate());
        service.resendInvoiceToXfileWithNewDate(request);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{id}/check-invoice-xfile-status")
    public ResponseEntity<ServiceOrderInvoiceResponseDto> checkInvoiceXfileStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.checkInvoiceXfileStatus(id));
    }

    @GetMapping("/from/{from}/to/{to}/update-invoice-xfile-statuses")
    public ResponseEntity<Void> updateInvoiceXfileStatus(@PathVariable LocalDate from, @PathVariable LocalDate to) {
        service.updateInvoiceXfileStatuses(from, to);
        return ResponseEntity.ok().build();
    }
}
