package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.onetimeorder.request.OneTimeOrderChangeStatusRequestDto;
import uz.uzkassa.cts.dto.onetimeorder.request.OneTimeRequestCreatDto;
import uz.uzkassa.cts.dto.onetimeorder.request.OneTimeRequestDto;
import uz.uzkassa.cts.dto.onetimeorder.response.OneTimeOrderResponseDto;
import uz.uzkassa.cts.dto.serviceorder.ServiceOrderListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.EmployeeReportFilter;
import uz.uzkassa.cts.filter.OneTimeOrderFilter;
import uz.uzkassa.cts.service.OneTimeOrderOldService;

@RestController
@RequestMapping("/api/one-time-order")
@Tag(name = "One Time Order")
@Slf4j
public class OneTimeOrderResource {

    private final OneTimeOrderOldService service;

    public OneTimeOrderResource(OneTimeOrderOldService service) {
        this.service = service;
    }

    @Operation(summary ="List")
    @GetMapping("/list")
    public ResponseEntity<Page<OneTimeOrderResponseDto>> getAll(OneTimeOrderFilter filter) {
        log.debug("REST request to get all OneTimeOrderFilter : {}", filter);
        return ResponseEntity.ok(service.list(filter));
    }

    @Operation(summary = "List By Employee")
    @GetMapping("/by-employee")
    public ResponseEntity<Page<ServiceOrderListDTO>> listByEmployee(EmployeeReportFilter filter) {
        log.debug("REST request to get one time orders by employee : {}", filter);
        return ResponseEntity.ok(service.listByEmployee(filter));
    }

    @Operation(summary ="Add")
    @PostMapping
    public ResponseEntity<OneTimeOrderResponseDto> create(@Valid @RequestBody OneTimeRequestCreatDto dto) {
        log.debug("REST request to create new OneTimeRequestDto: {}", dto);
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(summary = "Update")
    @PutMapping
    public ResponseEntity<OneTimeOrderResponseDto> update(@Valid @RequestBody OneTimeRequestDto dto) {
        log.debug("REST request to update OneTimeRequestDto : {}", dto);
        return ResponseEntity.ok(service.update(dto));
    }

    @Operation(summary ="Get")
    @GetMapping("/{id}")
    public ResponseEntity<OneTimeOrderResponseDto> getById(@PathVariable Long id) {
        log.debug("REST request to get OneTimeRequestDto : {}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary ="Upload Specification")
    @PostMapping("/{id}/upload")
    public ResponseEntity<OneTimeOrderResponseDto> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload file : {}", file);
        return ResponseEntity.ok(service.uploadFile(id, file));
    }

    @Operation(summary ="Generate Specification Template")
    @GetMapping("/{id}/generateTemplate")
    public ResponseEntity<String> generateSpecificationTemplate(@PathVariable Long id) {
        log.debug("REST request to get by Id: {}", id);
        String result = service.getSpecificationHtmlTemplate(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to to delete Order by id : {}", id);
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Change Status")
    @PostMapping("/changeStatus")
    public ResponseEntity<OneTimeOrderResponseDto> changeStatus(@Valid @RequestBody OneTimeOrderChangeStatusRequestDto dto) {
        log.debug("REST request to change status: {}", dto);
        return ResponseEntity.ok(service.changeStatus(dto));
    }

    @Operation(summary ="Get By Contract")
    @GetMapping("contract/{contractId}")
    public ResponseEntity<Page<OneTimeOrderResponseDto>> getByContractId(@PathVariable Long contractId, BaseFilter filter) {
        log.debug("REST request get by contarctId: {}", contractId);
        return ResponseEntity.ok(service.getByContractId(contractId, filter));
    }

    @Operation(summary ="download report")
    @GetMapping("/download-report")
    public ResponseEntity<byte[]> download(OneTimeOrderFilter filter) {
        log.debug("REST request to get list: {}", filter);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"OneTimeService.xlsx\"")
            .body(service.download(filter));
    }
}
