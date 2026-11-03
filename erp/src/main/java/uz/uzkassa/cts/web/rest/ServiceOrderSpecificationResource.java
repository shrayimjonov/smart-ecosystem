package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.orderspecification.request.OrderSpecificationChangeStatusDto;
import uz.uzkassa.cts.dto.orderspecification.request.OrderSpecificationRequestDto;
import uz.uzkassa.cts.dto.orderspecification.response.OrderSpecificationResponseDto;
import uz.uzkassa.cts.dto.orderspecification.response.ServiceOrderSpecificationListDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.ServiceOrderSpecificationOldService;

import java.io.IOException;

@RestController
@RequestMapping("/api/serviceOrderSpecification")
@Tag(name = "serviceOrderSpecification")
@Slf4j
public class ServiceOrderSpecificationResource {

    private final ServiceOrderSpecificationOldService service;

    public ServiceOrderSpecificationResource(ServiceOrderSpecificationOldService service) {
        this.service = service;
    }

    @Operation(summary ="Add new specification")
    @PostMapping
    public ResponseEntity<OrderSpecificationResponseDto> create(@Valid @RequestBody OrderSpecificationRequestDto dto) {
        log.debug("REST request to create new OrderDto: {}", dto);
        OrderSpecificationResponseDto result = service.create(dto);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="generate specification template")
    @GetMapping("/{id}/generateTemplate")
    public ResponseEntity<String> generateSpecificationTemplate(@PathVariable Long id) {
        log.debug("REST request to get by Id: {}", id);
        String result = service.getSpecificationHtmlTemplate(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="upload specification")
    @PostMapping("/{id}/upload")
    public ResponseEntity<OrderSpecificationResponseDto> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to upload file : {}", file);
        OrderSpecificationResponseDto result = service.uploadFile(id, file);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="download specification File")
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id, HttpServletRequest request) {
        log.debug("REST request to download file with id: {}", id);
        Resource resource = service.downloadFile(id);// Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
                .body(resource);
    }

    @Operation(summary = "Change Status")
    @PostMapping("/{id}/changeStatus")
    public ResponseEntity<OrderSpecificationResponseDto> changeStatus(@PathVariable Long id, @Valid @RequestBody OrderSpecificationChangeStatusDto dto) {
        log.debug("REST request to change status OrderSpecificationChangeStatusDto : {}", dto);
        OrderSpecificationResponseDto result = service.changeStatus(id, dto);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Update order")
    @PutMapping
    public ResponseEntity<OrderSpecificationRequestDto> update(@Valid @RequestBody OrderSpecificationRequestDto dto) {
        log.debug("REST request to update OrderDto : {}", dto);
        OrderSpecificationRequestDto result = service.update(dto);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="Get order")
    @GetMapping("/{id}")
    public ResponseEntity<OrderSpecificationResponseDto> getById(@PathVariable Long id) {
        log.debug("REST request to get CompanyDTO : {}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary ="Get order")
    @GetMapping("/contract/{contractId}")
    public ResponseEntity<Page<OrderSpecificationResponseDto>> getByContractId(@PathVariable Long contractId, BaseFilter filter) {
        log.debug("REST request to get by contractId : {}", contractId);
        return ResponseEntity.ok(service.getByContractId(contractId, filter));
    }

    @Operation(summary ="generate specification template")
    @GetMapping("/list")
    public ResponseEntity<Page<ServiceOrderSpecificationListDto>> list(BaseFilter filter) {
        return ResponseEntity.ok(service.list(filter));
    }
}
