package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.contract.request.ContractChangeStatusDto;
import uz.uzkassa.cts.dto.tradeinletterofguarantee.request.LetterOfGuaranteeRequestDto;
import uz.uzkassa.cts.dto.tradeinletterofguarantee.request.LetterOfGuaranteeUpdateRequestDto;
import uz.uzkassa.cts.dto.tradeinletterofguarantee.response.LetterOfGuaranteeResponseDto;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.service.TradeInLetterOfGuaranteeService;

import java.util.List;

@RestController
@RequestMapping("/api/trade-in/letter-of-guarantee")
@Tag(name = "trade in invoice")
@Slf4j
public class TradeInLetterOfGuaranteeResource {

    private final TradeInLetterOfGuaranteeService service;

    public TradeInLetterOfGuaranteeResource(TradeInLetterOfGuaranteeService service) {
        this.service = service;
    }

    @Operation(summary ="List of one time order")
    @GetMapping("/list")
    public ResponseEntity<Page<LetterOfGuaranteeResponseDto>> getAll(TradeInInvoiceFilter filter) {
        log.debug("REST request to get all OneTimeOrderFilter : {}", filter);
        return ResponseEntity.ok(service.list(filter));
    }

    @Operation(summary ="Get order")
    @GetMapping("/{id}")
    public ResponseEntity<LetterOfGuaranteeResponseDto> getById(@PathVariable Long id) {
        log.debug("REST request to get OneTimeRequestDto : {}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary ="Get order")
    @GetMapping("/customer/{customerId}/for-attach")
    public ResponseEntity<List<LetterOfGuaranteeResponseDto>> getByCustomerIdForAttach(@PathVariable Long customerId) {
        log.debug("REST request to get OneTimeRequestDto : {}", customerId);
        return ResponseEntity.ok(service.getByCustomerIdForAttach(customerId));
    }

    @Operation(summary ="Create")
    @PostMapping
    public ResponseEntity<LetterOfGuaranteeResponseDto> create(@Valid @RequestBody LetterOfGuaranteeRequestDto dto) {
        log.debug("REST request to create new TradeInDeviceDTO: {}", dto);
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(summary ="upload specification")
    @PostMapping("/{id}/upload")
    public ResponseEntity<LetterOfGuaranteeResponseDto> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload file : {}", file);
        return ResponseEntity.ok(service.uploadFile(id, file));
    }

    @Operation(summary ="generate specification template")
    @GetMapping("/{id}/generateTemplate")
    public ResponseEntity<String> generateTemplate(@PathVariable Long id) {
        log.debug("REST request to get by Id: {}", id);
        String result = service.getHtmlTemplate(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update")
    @PutMapping
    public ResponseEntity<LetterOfGuaranteeResponseDto> update(@Valid @RequestBody LetterOfGuaranteeUpdateRequestDto dto) {
        log.debug("REST request to update TradeInDeviceDTO : {}", dto);
        return ResponseEntity.ok(service.update(dto));
    }

    @Operation(summary ="change contract status")
    @PutMapping("/{id}/changeStatus")
    public ResponseEntity<LetterOfGuaranteeResponseDto> changeContractStatus(@PathVariable Long id, @Valid @RequestBody ContractChangeStatusDto dto) {
        return ResponseEntity.ok(service.changeStatus(id, dto));
    }
}
