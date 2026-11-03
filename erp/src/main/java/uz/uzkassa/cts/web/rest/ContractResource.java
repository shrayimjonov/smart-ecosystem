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
import uz.uzkassa.cts.dto.contract.request.ContractChangeStatusDto;
import uz.uzkassa.cts.dto.contract.request.ContractDto;
import uz.uzkassa.cts.dto.contract.request.ContractFilterRequestDto;
import uz.uzkassa.cts.dto.contract.response.ContactResponseDto;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.ContractService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@Tag(name = "Contract")
@Slf4j
public class ContractResource {

    private final ContractService service;

    public ContractResource(ContractService service) {
        this.service = service;
    }

    @Operation(summary ="Add Contract for buy")
    @PostMapping("/contractforbuy")
    public ResponseEntity<ContactResponseDto> createContractForBuy(@Valid @RequestBody ContractDto contractDto) {
        log.debug("REST request to create new ContractDTO: {}", contractDto);
        ContactResponseDto result = service.createContractForBuy(contractDto);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="Add Contract for Service")
    @PostMapping("/contractforservice")
    public ResponseEntity<ContactResponseDto> createContractForService(@Valid @RequestBody ContractDto contractDto) {
        log.debug("REST request to create new ContractDTO: {}", contractDto);
        ContactResponseDto result = service.createContractForService(contractDto);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="Add Contract for Service")
    @GetMapping("/customer/{id}/lastforbuy")
    public ResponseEntity<ContactResponseDto> getLastBuyContract(@PathVariable Long id) {
        log.debug("REST request to get lastforbuy: {}", id);
        ContactResponseDto result = service.getCustomerActiveContractForBuy(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="Add Contract for Service")
    @GetMapping("/customer/{id}/lastforservice")
    public ResponseEntity<ContactResponseDto> getLastServceiContract(@PathVariable Long id) {
        log.debug("REST request to get lastforservice: {}", id);
        ContactResponseDto result = service.getCustomerActiveContractForService(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="update Contract")
    @PutMapping
    public ResponseEntity<ContactResponseDto> update(@Valid @RequestBody ContractDto contractDto) {
        log.debug("REST request to update new ContractDTO: {}", contractDto);
        ContactResponseDto result = service.update(contractDto);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="get Contract")
    @GetMapping("/{id}")
    public ResponseEntity<ContactResponseDto> get(@PathVariable Long id) {
        log.debug("REST request to get by Id: {}", id);
        ContactResponseDto result = service.get(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="generate Contract template")
    @GetMapping("/{id}/generateTemplate")
    public ResponseEntity<String> generateContractForBuyTemplate(@PathVariable Long id) {
        log.debug("REST request to get by Id: {}", id);
        String result = service.getContractForBuyHtmlTemplate(id);
        return ResponseEntity.ok(result);
    }


    @Operation(summary ="get Contract by customer id")
    @GetMapping("/customer/{id}")
    public ResponseEntity<List<ContactResponseDto>> getByCustomerId(@PathVariable Long id) {
        log.debug("REST request to get by Id: {}", id);
        List<ContactResponseDto> result = service.getByCustomerId(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="change contract status")
    @PutMapping("/{id}/changeStatus")
    public ResponseEntity<ContactResponseDto> changeContractStatus(@PathVariable Long id, @Valid @RequestBody ContractChangeStatusDto dto) {
        log.debug("REST request to get by Id: {}", id);
        log.debug("REST request to contract change dto: {}", dto);
        ContactResponseDto result = service.changeContractStatus(id, dto);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="List order")
    @PostMapping("/list")
    public ResponseEntity<Page<ContactResponseDto>> getAll(@RequestBody ContractFilterRequestDto dto, BaseFilter filter) {
        log.debug("REST request to get all ContractFilterRequestDto : {}", dto);
        log.debug("REST request to get all BaseFilter : {}", filter);
        if (SecurityUtils.isCurrentUserInRole(Role.CUSTOMER_ADMIN.getRoleCode())) {
            log.debug("REST request from customer");
            return ResponseEntity.ok(service.listFrCustomer(dto, filter));
        } else
            return ResponseEntity.ok(service.listForCto(dto, filter));
    }

    @Operation(summary ="upload file")
    @PostMapping("/{id}/upload")
    public ResponseEntity<UploadFileResponse> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) {
        log.debug("REST request to get by Id: {}", id);
        log.debug("REST request to upload file : {}", file);
        UploadFileResponse response = service.uploadFile(file, id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary ="download file")
    @GetMapping("/downloadFile/{tin}/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, @PathVariable String tin, HttpServletRequest request) {
        log.debug("REST request to fileName: {}", fileName);
        log.debug("REST request to tin: {}", tin);
        // Load file as Resource
        Resource resource = service.loadFileAsResource(tin, fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.error("Could not determine file type {}", ex.getMessage(), ex);
        }

        // Fallback to the default content type if a type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
            .body(resource);
    }

}
